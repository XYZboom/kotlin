/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir.mock

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.sir.KotlinSourceReader
import org.jetbrains.kotlin.sir.SirOrigin
import org.jetbrains.kotlin.sir.SirParameter
import org.jetbrains.kotlin.sir.SirType

data class MockKotlinFunction(
    val fqName: FqName,
    val parameters: List<SirParameter>,
    val returnType: SirType,
    val documentation: String? = null,
) : SirOrigin.KotlinSources {
    override val path: List<String>
        get() = fqName.pathSegments().map { it.asString() }
}

object MockReader : KotlinSourceReader {
    override fun readParameters(from: SirOrigin.KotlinSources): List<SirParameter>? {
        from as MockKotlinFunction
        return from.parameters
    }

    override fun readReturnType(from: SirOrigin.KotlinSources): SirType? {
        from as MockKotlinFunction
        return from.returnType
    }

    override fun readDocumentation(from: SirOrigin.KotlinSources): String? {
        from as MockKotlinFunction
        return from.documentation
    }

}