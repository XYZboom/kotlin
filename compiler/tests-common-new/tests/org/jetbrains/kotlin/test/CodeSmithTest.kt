/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import com.github.xyzboom.codesmith.generator.GeneratorConfig
import com.github.xyzboom.codesmith.generator.impl.IrGeneratorImpl
import com.github.xyzboom.codesmith.mutator.impl.IrMutatorImpl
import com.github.xyzboom.codesmith.mutator.MutatorConfig
import com.github.xyzboom.codesmith.printer.IrProgramPrinter
import com.github.xyzboom.codesmith.runner.CoverageRunner
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirPsiBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.AbstractIrBlackBoxCodegenTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.runners.model.MultipleFailureException
import org.opentest4j.AssertionFailedError
import org.opentest4j.MultipleFailuresError
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.writeText
import kotlin.jvm.Throws
import kotlin.random.Random
import kotlin.time.measureTime

@OptIn(ExperimentalStdlibApi::class)
class CodeSmithTest : AbstractIrBlackBoxCodegenTest() {
    companion object {
        val logFile = File("/root/autodl-tmp/kotlin-log")
    }

    fun doOneRound(throwException: Boolean) {
        val printer = IrProgramPrinter()
        val prog = IrGeneratorImpl(
            GeneratorConfig(
                classNumRange = 5..9
            )
        ).genProgram()
        val fileContent = printer.printToSingle(prog)
        val tempFile = File.createTempFile("code-smith", ".kt")
        tempFile.writeText(fileContent)
        try {
            runTest(tempFile.absolutePath)
        } catch (e: Throwable) {
            e.printStackTrace()
            val dir = File(logFile, System.currentTimeMillis().toHexString())
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val writer = File(dir, "exception.txt").printWriter()
            e.printStackTrace(writer)
            writer.flush()
            File(dir, "main.kt").writeText(fileContent)
            File("codesmith-trace.log").copyTo(File(dir, "codesmith-trace.log"))
            if (throwException) {
                throw e
            }
        }
        /*val jacocoRuntimeData = CoverageRunner.getJacocoRuntimeData()
        val coverage = CoverageRunner.getBundleCoverage(
            jacocoRuntimeData,
            listOf("compiler", "core")
        )
        println(coverage.branchCounter.coveredCount)
        println(coverage.branchCounter.totalCount)*/
    }

    @Test
    fun test() {
        System.setProperty("java.io.tmpdir", "/root/autodl-tmp/tmp")
        var i = 1
        while (true) {
            val dur = measureTime { doOneRound(true) }
            println("${i++}:${dur}\t\t")
        }
    }
}