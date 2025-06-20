/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.compiler.plugins.kotlin.k2

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCallableReferenceAccessChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.utils.hasBackingField
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.references.toResolvedPropertySymbol

object ComposablePropertyChecker : FirPropertyChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirProperty) {
        // `@Composable` is only applicable to property getters, but in K1 we were also checking
        // properties with the annotation on the setter.
        if (declaration.getter?.hasComposableAnnotation(context.session) != true &&
            declaration.setter?.hasComposableAnnotation(context.session) != true
        ) {
            return
        }

        if (declaration.isVar) {
            reporter.reportOn(declaration.source, ComposeErrors.COMPOSABLE_VAR)
        }

        if (declaration.hasBackingField) {
            reporter.reportOn(
                declaration.source,
                ComposeErrors.COMPOSABLE_PROPERTY_BACKING_FIELD
            )
        }
    }
}

object ComposablePropertyReferenceChecker : FirCallableReferenceAccessChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirCallableReferenceAccess) {
        val property = expression.calleeReference.toResolvedPropertySymbol()
        if (
            property != null
            && !property.hasDelegate
            && property.isComposable(context.session)
        ) {
            reporter.reportOn(
                expression.source,
                ComposeErrors.COMPOSABLE_PROPERTY_REFERENCE
            )
        }
    }

}