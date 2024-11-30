/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import com.github.xyzboom.codesmith.generator.GeneratorConfig
import com.github.xyzboom.codesmith.generator.impl.IrGeneratorImpl
import com.github.xyzboom.codesmith.mutator.impl.IrMutatorImpl
import com.github.xyzboom.codesmith.mutator.MutatorConfig
import com.github.xyzboom.codesmith.ir.declarations.IrProgram
import com.github.xyzboom.codesmith.printer.IrPrinterToSingleFile
import com.github.xyzboom.codesmith.printer.java.IrJavaFilePrinter
import com.github.xyzboom.codesmith.printer.kt.IrKtFilePrinter
import com.github.xyzboom.codesmith.ir.types.IrFileType
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirPsiBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.AbstractIrBlackBoxCodegenTest
import org.junit.jupiter.api.Test
import org.junit.runners.model.MultipleFailureException
import org.opentest4j.AssertionFailedError
import org.opentest4j.MultipleFailuresError
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.random.Random
import kotlin.time.measureTime

class CodeSmithTest : AbstractIrBlackBoxCodegenTest() {
    @Test
    fun test() {
        for (i in 0..10000) {
            val printerSeed = 12
            println(i)
            val result: String
            val temp: Path
            val prog: IrProgram
            val genTime = measureTime {
                prog = IrGeneratorImpl(
                    config = GeneratorConfig(
                        moduleNumRange = 1..3,
                        fileNumRange = 1..2,
                        packageNumRange = 1..2,
                        classNumRange = 2..7,
                        constructorNumRange = 1..2
                    )
                ).generate()
                result = IrPrinterToSingleFile(
                    mapOf(
                        IrFileType.JAVA to IrJavaFilePrinter(),
                        IrFileType.KOTLIN to IrKtFilePrinter()
                    )
                ).print(prog)
                temp = Files.createTempFile("temp", ".kt")
                temp.writeText("$result\n// FILE: main__.kt\nfun box(): String = \"OK\"")
            }
            println(genTime)
            val testTime = measureTime {
                try {
                    runTest(temp.toAbsolutePath().toString())
                } catch (e: Throwable) {
                    println(result)
                    throw e
                }
            }
            println(testTime)
            val (mutatedProg, configResult) = IrMutatorImpl(
                config = MutatorConfig(
                    ktExposeKtInternal = false,
                    functionCallInternal = true
                )
            ).mutate(prog)
            if (!configResult.anyEnabled()) continue
            val mutatedResult = IrPrinterToSingleFile(
                mapOf(
                    IrFileType.JAVA to IrJavaFilePrinter(),
                    IrFileType.KOTLIN to IrKtFilePrinter()
                )
            ).print(mutatedProg)
            temp.writeText("$mutatedResult\n// FILE: main__.kt\nfun box(): String = \"OK\"")
            try {
                runTest(temp.toAbsolutePath().toString())
                throw CodeSmithMutationException()
            } catch (_: MultipleFailuresError) {
            } catch (_: AssertionFailedError) {
            } catch (_: CodeSmithMutationException) {
                // IDEA can help to auto compare mutated and original code
                throw AssertionFailedError("", result, mutatedResult)
            }
        }
    }
}