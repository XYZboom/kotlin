/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.native.toolchain

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.gradle.targets.native.internal.NativeDistributionTypeProvider
import org.jetbrains.kotlin.gradle.targets.native.internal.PlatformLibrariesGenerator
import org.jetbrains.kotlin.gradle.tasks.withType
import org.jetbrains.kotlin.gradle.utils.SingleActionPerProject
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File

internal interface UsesKotlinNativeBundleBuildService : Task {
    @get:Internal
    val kotlinNativeBundleBuildService: Property<KotlinNativeBundleBuildService>
}

/**
 * This service provides functionality to prepare a Kotlin/Native bundle.
 */
internal abstract class KotlinNativeBundleBuildService : BuildService<BuildServiceParameters.None> {

    companion object {
        fun registerIfAbsent(project: Project): Provider<KotlinNativeBundleBuildService> =
            project.rootProject.gradle.sharedServices.registerIfAbsent(
                "kotlinNativeBundleBuildService",
                KotlinNativeBundleBuildService::class.java
            ) { spec ->
                // we are specifying only one service to prevent concurrent modification of installed bundle
                spec.maxParallelUsages.set(1)
            }.also { serviceProvider ->
                SingleActionPerProject.run(project, UsesKotlinNativeBundleBuildService::class.java.name) {
                    project.tasks.withType<UsesKotlinNativeBundleBuildService>().configureEach { task ->
                        task.kotlinNativeBundleBuildService.value(serviceProvider).disallowChanges()
                        task.usesService(serviceProvider)
                    }
                }
            }
    }

    internal fun prepareKotlinNativeBundle(
        project: Project,
        kotlinNativeCompilerConfiguration: ConfigurableFileCollection,
        kotlinNativeVersion: String,
        bundleDir: File,
        reinstallFlag: Boolean,
        konanTargets: Set<KonanTarget>,
    ) {

        if (reinstallFlag) {
            bundleDir.deleteRecursively()
        }

        if (!bundleDir.resolve("bin").exists()) {
            val gradleCachesKotlinNativeDir =
                kotlinNativeCompilerConfiguration
                    .singleOrNull()
                    ?.resolve(kotlinNativeVersion)
                    ?: error(
                        "Kotlin Native dependency has not been properly resolved. " +
                                "Please, make sure that you've declared the repository, which contains $kotlinNativeVersion."
                    )

            project.logger.info("Moving Kotlin/Native bundle from tmp directory $gradleCachesKotlinNativeDir to ${bundleDir.absolutePath}")
            project.copy {
                it.from(gradleCachesKotlinNativeDir)
                it.into(bundleDir)
            }
            project.logger.info("Moved Kotlin/Native bundle from $gradleCachesKotlinNativeDir to ${bundleDir.absolutePath}")
        }

        project.setupKotlinNativeDependencies(konanTargets)
    }

    private fun Project.setupKotlinNativeDependencies(konanTargets: Set<KonanTarget>) {
        val distributionType = NativeDistributionTypeProvider(this).getDistributionType()
        if (distributionType.mustGeneratePlatformLibs) {
            konanTargets.forEach { konanTarget ->
                PlatformLibrariesGenerator(project, konanTarget).generatePlatformLibsIfNeeded()
            }
        }
    }
}