/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.sir.passes.translation

import org.jetbrains.kotlin.sir.*
import org.jetbrains.kotlin.sir.builder.buildFunction
import org.jetbrains.kotlin.sir.visitors.SirTransformerVoid
import org.jetbrains.sir.passes.SirPass


/**
 * Translates `SirForeignFunction` into `SirFunction`.
 * Works only with Nominal Types and top-level functions, currently.
 * If received `element` of different type than `SirForeignFunction`,
 * or `element` does not contain origin of type `SirOrigin.KotlinEntity.Function`,
 * returns original element.
 */
public class ForeignIntoSwiftFunctionTranslationPass(private val reader: KotlinSourceReader) :
    SirPass<SirElement, Nothing?, SirDeclaration> {

    private class Transformer(private val reader: KotlinSourceReader) : SirTransformerVoid() {
        override fun <E : SirElement> transformElement(element: E): E {
            element.transformChildren(this)
            return element
        }

        override fun transformForeignFunction(function: SirForeignFunction): SirDeclaration {
            val source = function.origin as? SirOrigin.KotlinSources
                ?: return function

            val sirParameters = reader.readParameters(source)
                ?: return function

            val sirReturnType = reader.readReturnType(source)
                ?: return function

            val sirDocumentation = reader.readDocumentation(source)

            return buildFunction {
                origin = function.origin
                visibility = function.visibility

                isStatic = function.parent is SirDeclaration
                name = source.path.last()

                sirParameters.mapTo(parameters) { it }
                returnType = sirReturnType
                documentation = sirDocumentation
            }.apply {
                parent = function.parent
            }
        }
    }

    override fun run(element: SirElement, data: Nothing?): SirDeclaration = element.transform(Transformer(reader))
}
