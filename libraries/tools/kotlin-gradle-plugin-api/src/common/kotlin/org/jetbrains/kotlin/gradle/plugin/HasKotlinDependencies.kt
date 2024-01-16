/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.logging.Logger
import java.io.File

interface KotlinDependencyHandler {
    val project: Project
    fun api(dependencyNotation: Any): Dependency?
    fun api(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency
    fun <T : Dependency> api(dependency: T, configure: T.() -> Unit): T
    fun api(dependencyNotation: String, configure: Closure<*>) = api(dependencyNotation) { project.configure(this, configure) }
    fun <T : Dependency> api(dependency: T, configure: Closure<*>) = api(dependency) { project.configure(this, configure) }

    fun implementation(dependencyNotation: Any): Dependency?
    fun implementation(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency
    fun <T : Dependency> implementation(dependency: T, configure: T.() -> Unit): T
    fun implementation(dependencyNotation: String, configure: Closure<*>) =
        implementation(dependencyNotation) { project.configure(this, configure) }

    fun <T : Dependency> implementation(dependency: T, configure: Closure<*>) =
        implementation(dependency) { project.configure(this, configure) }

    fun compileOnly(dependencyNotation: Any): Dependency?
    fun compileOnly(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency
    fun <T : Dependency> compileOnly(dependency: T, configure: T.() -> Unit): T
    fun compileOnly(dependencyNotation: String, configure: Closure<*>) =
        compileOnly(dependencyNotation) { project.configure(this, configure) }

    fun <T : Dependency> compileOnly(dependency: T, configure: Closure<*>) =
        compileOnly(dependency) { project.configure(this, configure) }

    fun runtimeOnly(dependencyNotation: Any): Dependency?
    fun runtimeOnly(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency
    fun <T : Dependency> runtimeOnly(dependency: T, configure: T.() -> Unit): T
    fun runtimeOnly(dependencyNotation: String, configure: Closure<*>) =
        runtimeOnly(dependencyNotation) { project.configure(this, configure) }

    fun <T : Dependency> runtimeOnly(dependency: T, configure: Closure<*>) =
        runtimeOnly(dependency) { project.configure(this, configure) }

    fun kotlin(simpleModuleName: String): ExternalModuleDependency = kotlin(simpleModuleName, null)
    fun kotlin(simpleModuleName: String, version: String?): ExternalModuleDependency

    fun project(path: String, configuration: String? = null): ProjectDependency =
        project(listOf("path", "configuration").zip(listOfNotNull(path, configuration)).toMap())

    fun project(notation: Map<String, Any?>): ProjectDependency

    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.enforcedPlatform(notation)")
    )
    fun enforcedPlatform(notation: Any): Dependency =
        project.dependencies.enforcedPlatform(notation)

    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.enforcedPlatform(notation, configureAction)")
    )
    fun enforcedPlatform(notation: Any, configureAction: Action<in Dependency>): Dependency =
        project.dependencies.enforcedPlatform(notation, configureAction)

    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.platform(notation)")
    )
    fun platform(notation: Any): Dependency =
        project.dependencies.platform(notation)

    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.platform(notation, configureAction)")
    )
    fun platform(notation: Any, configureAction: Action<in Dependency>): Dependency =
        project.dependencies.platform(notation, configureAction)

    @Deprecated("Dukat integration is in redesigning process. Now it does not work.")
    fun npm(
        name: String,
        version: String,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(name, version)
    }

    fun npm(
        name: String,
        version: String
    ): Dependency

    fun npm(
        name: String,
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(name, directory)
    }

    fun npm(
        name: String,
        directory: File
    ): Dependency

    fun npm(
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(directory)
    }

    fun npm(
        directory: File
    ): Dependency

    fun devNpm(
        name: String,
        version: String
    ): Dependency

    fun devNpm(
        name: String,
        directory: File
    ): Dependency

    fun devNpm(
        directory: File
    ): Dependency

    fun optionalNpm(
        name: String,
        version: String,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(name, version)
    }

    fun optionalNpm(
        name: String,
        version: String
    ): Dependency

    fun optionalNpm(
        name: String,
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(name, directory)
    }

    fun optionalNpm(
        name: String,
        directory: File
    ): Dependency

    fun optionalNpm(
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(directory)
    }

    fun optionalNpm(
        directory: File
    ): Dependency

    fun peerNpm(
        name: String,
        version: String
    ): Dependency
}

/**
 * Represents a Kotlin DSL entity having configurable Kotlin dependencies.
 */
interface HasKotlinDependencies {

    /**
     * Configure dependencies for this entity.
     */
    fun dependencies(configure: KotlinDependencyHandler.() -> Unit)

    /**
     * Configure dependencies for this entity.
     */
    fun dependencies(configure: Action<KotlinDependencyHandler>)

    /**
     * The name of the API Gradle configuration for this entity.
     *
     * The API configuration contains dependencies which are exported by this entity, and is not transitive by default.
     *
     * This configuration is not meant to be resolved.
     */
    val apiConfigurationName: String

    /**
     * The name of the implementation Gradle configuration for this entity.
     *
     * The implementation configuration should contain dependencies which are specific to the implementation of the component
     * (internal APIs).
     *
     * This configuration is not meant to be resolved.
     */
    val implementationConfigurationName: String

    /**
     * The name of the compile-only Gradle configuration for this entity.
     *
     * The compile-only configuration contains dependencies which are participating in compilation,
     * but should be added explicitly in the runtime.
     *
     * This configuration is not meant to be resolved.
     */
    val compileOnlyConfigurationName: String

    /**
     * The name of the runtime-only Gradle configuration for this entity.
     *
     * The runtime-only configuration contains dependencies which are not participating in the compilation,
     * but added in the runtime.
     *
     * This configuration is not meant to be resolved.
     */
    val runtimeOnlyConfigurationName: String
}

/**
 * @suppress
 */
@Deprecated(
    message = "Do not use in your build script",
    level = DeprecationLevel.ERROR
)
fun warnNpmGenerateExternals(logger: Logger) {
    logger.warn(
        """
        |
        |==========
        |Please note, Dukat integration in Gradle plugin does not work now.
        |It is in redesigning process.
        |==========
        |
        """.trimMargin()
    )
}
