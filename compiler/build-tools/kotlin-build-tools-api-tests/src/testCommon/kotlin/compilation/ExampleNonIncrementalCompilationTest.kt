/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.tests.compilation

import org.jetbrains.kotlin.buildtools.api.CompilerExecutionStrategyConfiguration
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertLogContainsPatterns
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertOutputs
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.BaseCompilationTest
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.DefaultStrategyAgnosticCompilationTest
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.LogLevel
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.project
import org.junit.jupiter.api.Disabled
import kotlin.io.path.writeText

class ExampleNonIncrementalCompilationTest : BaseCompilationTest() {
    @DefaultStrategyAgnosticCompilationTest
    fun myTest(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project {
            val module1 = module("jvm-module-1")
            val module2 = module("jvm-module-2", listOf(module1))

            module1.compile(strategyConfig) {
                assertOutputs("FooKt.class", "Bar.class", "BazKt.class")
            }
            module2.compile(strategyConfig) {
                assertOutputs("AKt.class", "BKt.class")
            }
        }
    }

    @DefaultStrategyAgnosticCompilationTest
    fun failedCompilationTest(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project {
            val module1 = module("jvm-module-1")

            module1.sourcesDirectory.resolve("bar.kt").writeText("aaaa")

            module1.compile(strategyConfig) {
                expectFail()
                assertLogContainsPatterns(LogLevel.ERROR, ".*bar\\.kt:1:1 Syntax error: Expecting a top level declaration.*".toRegex())
            }
        }
    }
}