/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.tests.compilation.scenario

import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.CompilationOutcome
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.LogLevel
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.Module

interface ScenarioModule {
    /**
     * Performs registered existing file modification.
     */
    fun changeFile(
        fileName: String,
        addedOutputs: Set<String> = emptySet(),
        removedOutputs: Set<String> = emptySet(),
        transform: (String) -> String,
    )

    /**
     * Performs registered file deletion.
     */
    fun deleteFile(fileName: String, removedOutputs: Set<String>)

    /**
     * Performs registered new file creation.
     */
    fun createFile(fileName: String, addedOutputs: Set<String>, content: String)

    fun compile(
        forceOutput: LogLevel? = null,
        assertions: context(Module) CompilationOutcome.() -> Unit = {},
    )
}