/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import com.github.xyzboom.codesmith.generator.GeneratorConfig
import com.github.xyzboom.codesmith.generator.impl.IrDeclGeneratorImpl
import com.github.xyzboom.codesmith.ir.types.builtin.IrBuiltInType
import com.github.xyzboom.codesmith.mutator.MutatorConfig
import com.github.xyzboom.codesmith.mutator.impl.IrMutatorImpl
import com.github.xyzboom.codesmith.printer.IrProgramPrinter
import com.github.xyzboom.codesmith.utils.nextBoolean
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirPsiBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.AbstractIrBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.runners.toKotlinTestInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import kotlin.reflect.jvm.jvmName
import kotlinx.coroutines.*
import org.jetbrains.kotlin.test.services.KotlinTestInfo
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime
import kotlin.random.Random
import kotlin.system.exitProcess

@OptIn(ExperimentalStdlibApi::class)
class CodeSmithDifferentialTest {
    companion object {
        val logFile = File(System.getProperty("codesmith.logger.outdir"))
    }

    interface IDifferentialTest {
        val jdk: TestJdkKind
        fun testProgram(fileContent: String): TestResult {
            val tempFile = File.createTempFile("code-smith", ".kt")
            tempFile.writeText("// JDK_KIND: ${jdk.name}\n$fileContent")
            try {
                runTest(tempFile.absolutePath)
            } catch (e: Throwable) {
                return TestResult(e, fileContent, this::class.simpleName!!)
            }
            return TestResult(null, fileContent, this::class.simpleName!!)
        }

        fun runTest(filePath: String)
        fun initTestInfo(testInfo: KotlinTestInfo)
    }

    object K1Jdk8Test : AbstractIrBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK
    }

    object K1Jdk11Test : AbstractIrBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK_11
    }

    object K1Jdk17Test : AbstractIrBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK_17
    }

    object K2Jdk8Test : AbstractFirPsiBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK
    }

    object K2Jdk11Test : AbstractFirPsiBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK_11
    }

    object K2Jdk17Test : AbstractFirPsiBlackBoxCodegenTest(), IDifferentialTest {
        override val jdk: TestJdkKind = TestJdkKind.FULL_JDK_17
    }

    private val testers = listOf<IDifferentialTest>(
        K1Jdk8Test, K1Jdk11Test, K1Jdk17Test,
        K2Jdk8Test, K2Jdk11Test, K2Jdk17Test,
    )

    @BeforeEach
    fun initTestInfo(testInfo: TestInfo) {
        testers.forEach { it.initTestInfo(testInfo.toKotlinTestInfo()) }
    }

    class TestResult(
        val e: Throwable?,
        val fileContent: String,
        val testName: String,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is TestResult) return false

            if (e == null && other.e != null) return false
            if (e != null && other.e == null) return false
//            if (e is JavaCompilationError) return other.e is JavaCompilationError
//            if (other.e is JavaCompilationError) return e is JavaCompilationError
            if (fileContent != other.fileContent) return false

            return true
        }

        override fun hashCode(): Int {
            var result = if (e == null) {
                0
            } else {
                1
            }
            result = 31 * result + fileContent.hashCode()
            return result
        }
    }

    fun AbstractKotlinCompilerTest.testProgram(fileContent: String): TestResult {
        val tempFile = File.createTempFile("code-smith", ".kt")
        tempFile.writeText(fileContent)
        try {
            runTest(tempFile.absolutePath)
        } catch (e: Throwable) {
            return TestResult(e, fileContent, this::class.jvmName)
        }
        return TestResult(null, fileContent, this::class.jvmName)
    }

    fun doOneRound(fileContent: String, throwException: Boolean) {
        val testResults = testers.map { it.testProgram(fileContent) }
        if (testResults.toSet().size != 1) {
            val dir = File(logFile, System.currentTimeMillis().toHexString())
            if (!dir.exists()) {
                dir.mkdirs()
            }
            File(dir, "main.kt").writeText(fileContent)
            File("codesmith-trace.log").copyTo(File(dir, "codesmith-trace.log"))
            for (result in testResults) {
                if (result.e != null) {
                    val writer = File(dir, "exception-${result.testName}.txt").printWriter()
                    result.e.printStackTrace(writer)
                    writer.flush()
                }
            }
            if (throwException) {
                throw RuntimeException()
            }
        }
    }

    @Test
    fun test() {
        val i = AtomicInteger(0)
        val throwException = false
        val parallelSize = 16
        runBlocking(Dispatchers.IO.limitedParallelism(parallelSize)) {
            val jobs = mutableListOf<Job>()
            repeat(parallelSize) {
                val job = launch {
                    var enableGeneric: Boolean
                    val threadName = Thread.currentThread().name
                    while (true) {
                        enableGeneric = Random.nextBoolean(0.15f)
//                        enableGeneric = false
                        val printer = IrProgramPrinter()
                        val generator = IrDeclGeneratorImpl(
                            GeneratorConfig(
                                /*classHasTypeParameterProbability = if (enableGeneric) {
                                    Random.nextFloat() / 4f
                                } else {
                                    0f
                                }*/
                            )
                        )
                        val prog = generator.genProgram()
                        repeat(5) {
                            val fileContent = printer.printToSingle(prog)
                            val dur = measureTime { doOneRound(fileContent, throwException) }
                            println("$threadName ${i.incrementAndGet()}:${dur}\t\t")
                            generator.shuffleLanguage(prog)
                        }
                        /*val config = MutatorConfig.allZero.copy(
                            mutateParameterNullabilityWeight = 1
                        )*/
                        val config = MutatorConfig.default
                        val mutator = IrMutatorImpl(
                            config,
                            generator = generator
                        )
                        if (mutator.mutate(prog)) {
                            repeat(5) {
                                val fileContent = printer.printToSingle(prog)
                                val dur = measureTime { doOneRound(fileContent, throwException) }
                                println("$threadName ${i.incrementAndGet()}:${dur}\t\t")
                                generator.shuffleLanguage(prog)
                            }
                        }
                    }
                }
                jobs.add(job)
            }
            jobs.joinAll()
        }
    }
}
