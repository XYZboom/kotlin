/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import com.github.xyzboom.codesmith.generator.GeneratorConfig
import com.github.xyzboom.codesmith.generator.impl.IrDeclGeneratorImpl
import com.github.xyzboom.codesmith.ir.IrProgram
import com.github.xyzboom.codesmith.mutator.impl.IrMutatorImpl
import com.github.xyzboom.codesmith.mutator.MutatorConfig
import com.github.xyzboom.codesmith.printer.IrProgramPrinter
import com.github.xyzboom.codesmith.runner.CoverageRunner
import com.github.xyzboom.codesmith.serde.defaultIrMapper
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
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.writeText
import kotlin.jvm.Throws
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlinx.coroutines.*

@OptIn(ExperimentalStdlibApi::class)
class CodeSmithTest : AbstractIrBlackBoxCodegenTest() {
    companion object {
        val logFile = File(System.getProperty("codesmith.logger.outdir"))
    }

    fun doOneRound(throwException: Boolean): Pair<String, Duration> {
        val printer = IrProgramPrinter()
        val prog = IrDeclGeneratorImpl(
            GeneratorConfig()
        ).genProgram()
        val fileContent = printer.printToSingle(prog)
        val tempFile = File.createTempFile("code-smith", ".kt")
        tempFile.writeText("// JDK_KIND: FULL_JDK_11\n" + fileContent)
        val dur: Duration = try {
            measureTime {
                runTest(tempFile.absolutePath)
            }
        } catch (e: Throwable) {
            recordException(e, fileContent, throwException, prog)
        }
        return fileContent to dur
        /*val jacocoRuntimeData = CoverageRunner.getJacocoRuntimeData()
        val coverage = CoverageRunner.getBundleCoverage(
            jacocoRuntimeData,
            listOf("compiler", "core")
        )
        println(coverage.branchCounter.coveredCount)
        println(coverage.branchCounter.totalCount)*/
    }

    private fun recordException(
        e: Throwable, fileContent: String, throwException: Boolean,
        prog: IrProgram
    ): Duration {
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
        val jsonFile = File(dir, "program.json")
        defaultIrMapper.writeValue(jsonFile, prog)
        if (throwException) {
            throw e
        }
        return Duration.ZERO
    }

    @Test
    fun test() {
        val i = AtomicInteger(1)
        val throwException = true
        val parallelSize = 1
        doOneRound(throwException)
        runBlocking(Dispatchers.IO.limitedParallelism(parallelSize)) {
            val jobs = mutableListOf<Job>()
            repeat(32) {
                val job = launch {
                    while (true) {
                        val (fileContent, dur) = doOneRound(throwException)
                        println("${Thread.currentThread().name} ${i.incrementAndGet()}:${dur}\t\t")
                        /*if (dur > Duration.parse("2s")) {
                            val dir = File(logFile, System.currentTimeMillis().toHexString())
                            if (!dir.exists()) {
                                dir.mkdirs()
                            }
                            File(dir, "timeout").writeText("timeout: $dur")
                            File(dir, "main.kt").writeText(fileContent)
                            File("codesmith-trace.log").copyTo(File(dir, "codesmith-trace.log"))
                        }*/
                    }
                }
                jobs.add(job)
            }
            jobs.joinAll()
        }
    }
}