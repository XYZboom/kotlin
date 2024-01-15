/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.testFramework.IrFirConfigurableTestCase
import java.io.File

abstract class AbstractLoadJavaWithPsiClassReadingTest : AbstractLoadJavaTest(), IrFirConfigurableTestCase {
    override fun updateConfiguration(configuration: CompilerConfiguration) {
        configureIrFir(configuration)
        super.updateConfiguration(configuration)
    }

    override fun usePsiClassFilesReading() = true

    override fun getExpectedFile(expectedFileName: String): File {
        val differentResultFile = KotlinTestUtils.replaceExtension(File(expectedFileName), "psi.txt")
        if (differentResultFile.exists()) return differentResultFile
        return super.getExpectedFile(expectedFileName)
    }
}
