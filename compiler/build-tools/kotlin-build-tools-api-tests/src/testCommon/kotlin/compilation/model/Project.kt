/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.tests.compilation.model

import org.jetbrains.kotlin.buildtools.api.CompilationService
import org.jetbrains.kotlin.buildtools.api.ProjectId
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory

class Project(
    val projectDirectory: Path,
) {
    val projectId = ProjectId.ProjectUUID(UUID.randomUUID())
    val compilationService = CompilationService.loadImplementation(this.javaClass.classLoader)

    fun module(
        moduleName: String,
        dependencies: List<Module> = emptyList(),
        additionalCompilationArguments: List<String> = emptyList(),
    ): Module {
        val moduleDirectory = projectDirectory.resolve(moduleName)
        val module = JvmModule(this, moduleName, moduleDirectory, dependencies, additionalCompilationArguments)
        module.sourcesDirectory.createDirectories()
        val templatePath = Paths.get("src/testCommon/resources/modules/$moduleName")
        assert(templatePath.isDirectory()) {
            "Template for $moduleName not found. Expected template directory path is $templatePath"
        }
        templatePath.copyToRecursively(module.sourcesDirectory, followLinks = false)
        return module
    }

    fun endCompilationRound() {
        compilationService.finishProjectCompilation(projectId)
    }
}

fun BaseCompilationTest.project(action: Project.() -> Unit) {
    Project(workingDirectory).apply {
        action()
        endCompilationRound()
    }
}