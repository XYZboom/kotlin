/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir.analysisapi.transformers

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.*
import org.jetbrains.kotlin.analysis.api.types.KtType
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.sir.*
import org.jetbrains.kotlin.sir.builder.buildForeignFunction
import org.jetbrains.kotlin.sir.constants.*
import org.jetbrains.kotlin.sir.util.SirSwiftModule
import java.lang.IllegalStateException

public fun KtNamedFunction.toForeignFunction(): SirForeignFunction = analyze(this) {
    buildForeignFunction {
        origin = AAKotlinSource(symbol = this@toForeignFunction.getFunctionLikeSymbol())
    }
}

public object KotlinSourceReaderFromAA: KotlinSourceReader {
    override fun readParameters(from: SirOrigin.KotlinSources): List<SirParameter>? = from.readParameters()

    override fun readReturnType(from: SirOrigin.KotlinSources): SirType? = from.readReturnType()

    override fun readDocumentation(from: SirOrigin.KotlinSources): String? = from.readDocumentation()
}

private fun SirOrigin.KotlinSources.readParameters(): List<SirParameter>? = getFunctionLikeSymbol()?.valueParameters?.map { it.toSir() }

private fun SirOrigin.KotlinSources.readReturnType(): SirType? = getCallableSymbol()?.returnType?.toSir()

private fun SirOrigin.KotlinSources.readDocumentation(): String? = getCallableSymbol()?.psiSafe<KtDeclaration>()?.docComment?.text

private data class AAKotlinSource(
    val symbol: KtSymbol
) : SirOrigin.KotlinSources {
    override val path: List<String>
        get() = getCallableSymbol()?.psiSafe<KtNamedDeclaration>()?.fqName?.pathSegments()?.map { it.asString() } ?: emptyList()
}

private fun SirOrigin.KotlinSources.getFunctionLikeSymbol(): KtFunctionLikeSymbol? {
    if (this !is AAKotlinSource) return null
    val functionSymbol = symbol as? KtFunctionLikeSymbol ?: return null
    return functionSymbol
}

private fun SirOrigin.KotlinSources.getCallableSymbol(): KtCallableSymbol? {
    if (this !is AAKotlinSource) return null
    val functionSymbol = symbol as? KtCallableSymbol ?: return null
    return functionSymbol
}

private fun KtValueParameterSymbol.toSir(): SirParameter = SirParameter(
    argumentName = name.asString(),
    type = returnType.toSir(),
)

private fun KtType.toSir(): SirType = SirNominalType(
    type = when (toString()) {
        BYTE -> SirSwiftModule.int8
        SHORT -> SirSwiftModule.int16
        INT -> SirSwiftModule.int32
        LONG -> SirSwiftModule.int64
        BOOLEAN -> SirSwiftModule.bool
        DOUBLE -> SirSwiftModule.double
        FLOAT -> SirSwiftModule.float
        else -> throw IllegalStateException("unknown externally defined type")
    }
)
