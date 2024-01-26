/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.tests

import org.jetbrains.kotlin.backend.konan.testUtils.HeaderGenerator
import org.jetbrains.kotlin.backend.konan.testUtils.stdLibTypesDir
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.fail

class ObjCExportStdLibTypesTest(
    private val generator: HeaderGenerator,
) {

    @Test
    fun `test - stringBuilder`() {
        doTest(stdLibTypesDir.resolve("stringBuilder"))
    }

    @Test
    fun `test - iterator`() {
        doTest(stdLibTypesDir.resolve("iterator"))
    }

    @Test
    fun `test - array`() {
        doTest(stdLibTypesDir.resolve("array"))
    }

    private fun doTest(root: File) {
        if (!root.isDirectory) fail("Expected ${root.absolutePath} to be directory")
        val generatedHeaders = generator.generateHeaders(root, HeaderGenerator.Configuration()).toString()
        KotlinTestUtils.assertEqualsToFile(root.resolve("!${root.nameWithoutExtension}.h"), generatedHeaders)
    }
}