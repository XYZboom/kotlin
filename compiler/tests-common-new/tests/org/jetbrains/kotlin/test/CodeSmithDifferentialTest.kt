/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
import com.github.xyzboom.codesmith.generator.GeneratorConfig
import com.github.xyzboom.codesmith.generator.impl.IrDeclGeneratorImpl
import com.github.xyzboom.codesmith.printer.IrProgramPrinter
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirPsiBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.AbstractIrBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.codegen.AbstractFirLightTreeBlackBoxCodegenTest
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.runners.toKotlinTestInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import kotlin.reflect.jvm.jvmName
import kotlin.time.measureTime

@OptIn(ExperimentalStdlibApi::class)
class CodeSmithDifferentialTest {
    companion object {
        val logFile = File(System.getProperty("codesmith.logger.outdir"))
    }

    val irTest = object : AbstractIrBlackBoxCodegenTest() {}
    val firPsiTest = object : AbstractFirPsiBlackBoxCodegenTest() {}
    val firLightTreeTest = object : AbstractFirLightTreeBlackBoxCodegenTest() {}
    val testers = listOf(irTest, firPsiTest, firLightTreeTest)

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
        var i = 1
        val throwException = false
        while (true) {
            val printer = IrProgramPrinter()
            val generator = IrDeclGeneratorImpl(
                GeneratorConfig()
            )
            val prog = generator.genProgram()
            repeat(5) {
                val fileContent = printer.printToSingle(prog)
                val dur = measureTime { doOneRound(fileContent, throwException) }
                println("${i++}: $dur")
                generator.shuffleLanguage(prog)
            }
        }
    }
}