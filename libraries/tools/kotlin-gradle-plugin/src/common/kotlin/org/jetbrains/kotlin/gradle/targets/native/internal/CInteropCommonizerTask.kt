/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.native.internal

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.build.report.metrics.BuildMetricsReporter
import org.jetbrains.kotlin.build.report.metrics.GradleBuildPerformanceMetric
import org.jetbrains.kotlin.build.report.metrics.GradleBuildTime
import org.jetbrains.kotlin.commonizer.*
import org.jetbrains.kotlin.compilerRunner.GradleCliCommonizer
import org.jetbrains.kotlin.compilerRunner.KotlinNativeCommonizerToolRunner
import org.jetbrains.kotlin.compilerRunner.KotlinToolRunner
import org.jetbrains.kotlin.compilerRunner.konanHome
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtensionOrNull
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinSharedNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.sources.withDependsOnClosure
import org.jetbrains.kotlin.gradle.report.GradleBuildMetricsReporter
import org.jetbrains.kotlin.gradle.targets.native.internal.CInteropCommonizerTask.CInteropCommonizerDependencies
import org.jetbrains.kotlin.gradle.targets.native.internal.CInteropCommonizerTask.CInteropGist
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.gradle.utils.*
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import javax.inject.Inject

private typealias GroupedCommonizerDependencies = Map<CInteropCommonizerGroup, List<CInteropCommonizerDependencies>>


@CacheableTask
internal open class CInteropCommonizerTask
@Inject constructor(
    private val objectFactory: ObjectFactory,
    private val execOperations: ExecOperations,
    private val projectLayout: ProjectLayout
) : AbstractCInteropCommonizerTask() {

    internal class CInteropGist(
        @get:Input val identifier: CInteropIdentifier,
        @get:Input val konanTarget: KonanTarget,
        sourceSets: Provider<Set<KotlinSourceSet>>,

        @get:Classpath
        val libraryFile: Provider<File>,

        @get:Classpath
        val dependencies: FileCollection
    ) {
        @Suppress("unused") // Used for UP-TO-DATE check
        @get:Input
        val allSourceSetNames: Provider<List<String>> = sourceSets
            .map { it.withDependsOnClosure.map(Any::toString) }

        /** Autogenerated with IDEA */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CInteropGist

            if (identifier != other.identifier) return false
            if (konanTarget != other.konanTarget) return false
            if (libraryFile != other.libraryFile) return false
            if (dependencies != other.dependencies) return false
            if (allSourceSetNames != other.allSourceSetNames) return false

            return true
        }

        /** Autogenerated with IDEA */
        override fun hashCode(): Int {
            var result = identifier.hashCode()
            result = 31 * result + konanTarget.hashCode()
            result = 31 * result + libraryFile.hashCode()
            result = 31 * result + dependencies.hashCode()
            result = 31 * result + allSourceSetNames.hashCode()
            return result
        }

    }

    override val outputDirectory: File get() = projectLayout.buildDirectory.get().asFile.resolve("classes/kotlin/commonizer")

    @get:Internal
    internal val kotlinPluginVersion: Property<String> = objectFactory
        .property<String>()
        .chainedFinalizeValueOnRead()

    @get:Classpath
    internal val commonizerClasspath: ConfigurableFileCollection = objectFactory.fileCollection()

    @get:Input
    internal val customJvmArgs: ListProperty<String> = objectFactory
        .listProperty<String>()
        .chainedFinalizeValueOnRead()

    private val runnerSettings: Provider<KotlinNativeCommonizerToolRunner.Settings> = kotlinPluginVersion
        .zip(customJvmArgs) { pluginVersion, customJvmArgs ->
            KotlinNativeCommonizerToolRunner.Settings(
                pluginVersion,
                commonizerClasspath.files,
                customJvmArgs
            )
        }

    private val konanHome = project.file(project.konanHome)
    private val commonizerLogLevel = project.commonizerLogLevel
    private val additionalCommonizerSettings = project.additionalCommonizerSettings

    data class CInteropCommonizerDependencies(
        val commonizerTarget: CommonizerTarget,
        val dependencies: FileCollection
    )

    /**
     * For Gradle Configuration Cache support the Group-to-Dependencies relation should be pre-cached.
     * It is used during execution phase.
     */
    private val groupedCommonizerDependencies: Future<GroupedCommonizerDependencies> = project.lazyFuture {
        val multiplatformExtension = project.multiplatformExtensionOrNull ?: return@lazyFuture emptyMap()

        val sourceSetsByTarget = multiplatformExtension.sourceSets.groupBy { sourceSet -> sourceSet.commonizerTarget.getOrThrow() }
        val sourceSetsByGroup = multiplatformExtension.sourceSets.groupBy { sourceSet ->
            CInteropCommonizerDependent.from(sourceSet)?.let { findInteropsGroup(it) }
        }
        allInteropGroups.await().associateWith { group ->
            (group.targets + group.targets.allLeaves()).map { target ->
                val externalDependencyFiles: List<FileCollection> = when (target) {
                    is LeafCommonizerTarget -> {
                        cinterops.get()
                            .filter { cinterop -> cinterop.identifier in group.interops && cinterop.konanTarget == target.konanTarget }
                            .map { cinterop -> cinterop.dependencies }
                    }

                    is SharedCommonizerTarget -> {
                        val targetSourceSets = sourceSetsByTarget[target].orEmpty()
                        val groupSourceSets = sourceSetsByGroup[group].orEmpty().toSet()
                        targetSourceSets.intersect(groupSourceSets)
                            .filterIsInstance<DefaultKotlinSourceSet>()
                            /*
                            We take dependencies just from a single matching source set!
                            This is because all source sets matching the target and group constraints
                            will provide the same dependencies (since cinterops are just based upon KonanTarget)
                             */
                            .take(1)
                            .map { sourceSet -> project.createCInteropMetadataDependencyClasspath(sourceSet) }
                    }
                }

                CInteropCommonizerDependencies(
                    target, project.files(externalDependencyFiles, project.getNativeDistributionDependencies(target))
                )
            }
        }
    }

    @Suppress("unused") // Used for UP-TO-DATE check
    @get:Classpath
    protected val commonizerDependenciesClasspath: FileCollection = project.filesProvider {
        groupedCommonizerDependencies.getOrThrow().values.flatten().map { it.dependencies }
    }

    @get:Nested
    internal val cinterops: SetProperty<CInteropGist> = objectFactory.setProperty<CInteropGist>()

    @get:OutputDirectories
    val allOutputDirectories: Set<File>
        get() = allInteropGroups.getOrThrow().map { outputDirectory(it) }.toSet()

    @get:Internal
    val metrics: Property<BuildMetricsReporter<GradleBuildTime, GradleBuildPerformanceMetric>> = project.objects
        .property(GradleBuildMetricsReporter())

    internal fun from(task: TaskProvider<CInteropProcess>) {
        dependsOn(task)
        cinterops.add(task.map { it.toGist() })

    }

    @TaskAction
    protected fun commonizeCInteropLibraries() {
        allInteropGroups.getOrThrow().forEach(::commonize)
    }

    private fun commonize(group: CInteropCommonizerGroup) {
        val cinteropsForTarget = cinterops.get().filter { cinterop -> cinterop.identifier in group.interops }
        outputDirectory(group).deleteRecursively()
        if (cinteropsForTarget.isEmpty()) return

        val commonizerRunner = KotlinNativeCommonizerToolRunner(
            context = KotlinToolRunner.GradleExecutionContext.fromTaskContext(objectFactory, execOperations, logger),
            settings = runnerSettings.get(),
            metricsReporter = metrics.get(),
        )

        GradleCliCommonizer(commonizerRunner).commonizeLibraries(
            konanHome = konanHome,
            outputTargets = group.targets,
            inputLibraries = cinteropsForTarget.map { it.libraryFile.get() }.filter { it.exists() }.toSet(),
            dependencyLibraries = getCInteropCommonizerGroupDependencies(group),
            outputDirectory = outputDirectory(group),
            logLevel = commonizerLogLevel,
            additionalSettings = additionalCommonizerSettings,
        )
    }

    private fun getCInteropCommonizerGroupDependencies(group: CInteropCommonizerGroup): Set<CommonizerDependency> {
        val dependencies = groupedCommonizerDependencies.getOrThrow()[group]
            ?.flatMap { (target, dependencies) ->
                dependencies.files
                    .filter { file -> file.exists() && (file.isDirectory || file.extension == "klib") }
                    .map { file -> TargetedCommonizerDependency(target, file) }
            }
            ?.toSet()
        requireNotNull(dependencies) { "Unexpected $group" }

        return dependencies
    }

    @get:Internal
    internal val allInteropGroups: Future<Set<CInteropCommonizerGroup>> = project.lazyFuture {
        val dependents = allDependents.await()
        val allScopeSets = dependents.map { it.scopes }.toSet()
        val rootScopeSets = allScopeSets.filter { scopeSet ->
            allScopeSets.none { otherScopeSet -> otherScopeSet != scopeSet && otherScopeSet.containsAll(scopeSet) }
        }

        rootScopeSets.map { scopeSet ->
            val dependentsForScopes = dependents.filter { dependent ->
                scopeSet.containsAll(dependent.scopes)
            }

            CInteropCommonizerGroup(
                targets = dependentsForScopes.map { it.target }.toSet(),
                interops = dependentsForScopes.flatMap { it.interops }.toSet()
            )
        }.toSet()
    }

    @get:Nested
    @Suppress("unused") // UP-TO-DATE check
    protected val allInteropGroupsOrThrow get() = allInteropGroups.getOrThrow()

    override suspend fun findInteropsGroup(dependent: CInteropCommonizerDependent): CInteropCommonizerGroup? {
        val suitableGroups = allInteropGroups.await().filter { group ->
            group.interops.containsAll(dependent.interops) && group.targets.contains(dependent.target)
        }

        assert(suitableGroups.size <= 1) {
            "CInteropCommonizerTask: Unnecessary work detected: More than one suitable group found for cinterop dependent."
        }

        return suitableGroups.firstOrNull()
    }

    private val allDependents: Future<Set<CInteropCommonizerDependent>> = project.lazyFuture {
        val multiplatformExtension = project.multiplatformExtensionOrNull ?: return@lazyFuture emptySet()

        val fromSharedNativeCompilations = multiplatformExtension
            .targets.flatMap { target -> target.compilations }
            .filterIsInstance<KotlinSharedNativeCompilation>()
            .mapNotNull { compilation -> CInteropCommonizerDependent.from(compilation) }
            .toSet()

        val fromSourceSets = multiplatformExtension.awaitSourceSets()
            .mapNotNull { sourceSet -> CInteropCommonizerDependent.from(sourceSet) }
            .toSet()

        val fromSourceSetsAssociateCompilations = multiplatformExtension.awaitSourceSets()
            .mapNotNull { sourceSet -> CInteropCommonizerDependent.fromAssociateCompilations(sourceSet) }
            .toSet()

        (fromSharedNativeCompilations + fromSourceSets + fromSourceSetsAssociateCompilations)
    }
}

private fun CInteropProcess.toGist(): CInteropGist {
    return CInteropGist(
        identifier = settings.identifier,
        konanTarget = konanTarget,
        // FIXME support cinterop with PM20
        sourceSets = project.provider {
            project.multiplatformExtension.targets.getByName(targetName)
                .compilations.getByName(compilationName).kotlinSourceSets
        },
        libraryFile = outputFileProvider,
        dependencies = libraries
    )
}
