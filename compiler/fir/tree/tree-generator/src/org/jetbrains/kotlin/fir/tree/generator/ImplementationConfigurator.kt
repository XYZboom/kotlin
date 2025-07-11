/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.tree.generator

import org.jetbrains.kotlin.fir.tree.generator.FirTree.declaration
import org.jetbrains.kotlin.fir.tree.generator.context.AbstractFirTreeImplementationConfigurator
import org.jetbrains.kotlin.fir.tree.generator.model.Element
import org.jetbrains.kotlin.fir.tree.generator.model.Field
import org.jetbrains.kotlin.fir.tree.generator.model.Implementation
import org.jetbrains.kotlin.generators.tree.ImplementationKind.Object
import org.jetbrains.kotlin.generators.tree.ImplementationKind.OpenClass
import org.jetbrains.kotlin.generators.tree.config.AbstractImplementationConfigurator

object ImplementationConfigurator : AbstractFirTreeImplementationConfigurator() {

    override fun configure(model: Model) = with(FirTree) {

        impl(receiverParameter)

        impl(constructor) {
            defaultFalse("isPrimary", withGetter = true)
        }

        impl(constructor, "FirPrimaryConstructor") {
            publicImplementation()
            defaultTrue("isPrimary", withGetter = true)
        }

        impl(errorPrimaryConstructor) {
            publicImplementation()
            defaultTrue("isPrimary", withGetter = true)
        }

        impl(outerClassTypeParameterRef) {
            publicImplementation()
        }
        impl(constructedClassTypeParameterRef)

        noImpl(declarationStatus)
        noImpl(resolvedDeclarationStatus)

        impl(regularClass) {
            defaultFalse("hasLazyNestedClassifiers", withGetter = true)
        }

        impl(anonymousInitializer)

        impl(anonymousObject)
        impl(danglingModifierList)
        noImpl(anonymousObjectExpression)

        impl(typeAlias)

        impl(import)

        impl(resolvedImport) {
            delegateFields(listOf("aliasName", "aliasSource", "importedFqName", "isAllUnder"), "delegate")

            default("source") {
                delegate = "delegate"
            }

            default("resolvedParentClassId") {
                delegate = "relativeParentClassName"
                delegateCall = "let { ClassId(packageFqName, it, isLocal = false) }"
                withGetter = true
            }

            default("importedName") {
                delegate = "importedFqName"
                delegateCall = "shortName()"
                withGetter = true
            }
        }

        fun ImplementationContext.commonAnnotationConfig() {
            defaultEmptyList("annotations", withGetter = true)
            default("coneTypeOrNull") {
                value = "annotationTypeRef.coneTypeOrNull"
                withGetter = true
            }
            additionalImports(coneTypeOrNullImport)
        }

        impl(annotation) {
            commonAnnotationConfig()
        }

        impl(annotationCall) {
            commonAnnotationConfig()
        }

        impl(errorAnnotationCall) {
            commonAnnotationConfig()
            default("annotationResolvePhase") {
                value = "FirAnnotationResolvePhase.Types"
            }
        }

        impl(arrayLiteral)

        impl(callableReferenceAccess)

        impl(componentCall) {
            default("calleeReference", "FirSimpleNamedReference(source, Name.identifier(\"component\$componentIndex\"))")
            additionalImports(simpleNamedReferenceType, nameType)
            optInToInternals()
        }

        impl(whileLoop)

        impl(doWhileLoop)

        impl(delegatedConstructorCall) {
            default("isSuper") {
                value = "!isThis"
                withGetter = true
            }
            default("coneTypeOrNull") {
                value = "ConeClassLikeTypeImpl(StandardClassIds.Unit.toLookupTag(), typeArguments = emptyArray(), isMarkedNullable = false)"
                isMutable = false
            }
            additionalImports(
                explicitThisReferenceType, explicitSuperReferenceType, coneClassLikeTypeImplType,
                standardClassIdsType, toSymbolUtilityFunction
            )
        }

        impl(multiDelegatedConstructorCall) {
            default("source") {
                value = "delegatedConstructorCalls.last().source"
                withGetter = true
            }
            default("annotations") {
                value = "delegatedConstructorCalls.last().annotations"
                withGetter = true
            }
            default("argumentList") {
                value = "delegatedConstructorCalls.last().argumentList"
                withGetter = true
            }
            default("contextArguments") {
                value = "delegatedConstructorCalls.last().contextArguments"
                withGetter = true
            }
            default("constructedTypeRef") {
                value = "delegatedConstructorCalls.last().constructedTypeRef"
                withGetter = true
            }
            default("dispatchReceiver") {
                value = "delegatedConstructorCalls.last().dispatchReceiver"
                withGetter = true
            }
            default("calleeReference") {
                value = "delegatedConstructorCalls.last().calleeReference"
                withGetter = true
            }
            default("isThis") {
                value = "delegatedConstructorCalls.last().isThis"
                withGetter = true
            }
            default("isSuper") {
                value = "!isThis"
                withGetter = true
            }
            default("coneTypeOrNull") {
                value = "delegatedConstructorCalls.last().coneTypeOrNull"
                withGetter = true
            }
            publicImplementation()
        }

        impl(delegatedConstructorCall, "FirLazyDelegatedConstructorCall") {
            val error = """error("FirLazyDelegatedConstructorCall should be calculated before accessing")"""
            default("source") {
                value = error
                withGetter = true
            }
            default("annotations") {
                value = error
                withGetter = true
            }
            default("argumentList") {
                value = error
                withGetter = true
            }
            default("contextArguments") {
                value = error
                withGetter = true
            }
            default("dispatchReceiver") {
                value = error
                withGetter = true
            }
            default("isSuper") {
                value = "!isThis"
                withGetter = true
            }
            publicImplementation()
        }

        impl(expression, "FirElseIfTrueCondition") {
            defaultBuiltInType("Boolean")
            additionalImports(implicitBooleanTypeRefType)
            publicImplementation()
        }

        impl(block) {
            isMutable("isUnitCoerced")
            default("isUnitCoerced") {
                value = "false"
            }
        }

        val emptyExpressionBlock = impl(block, "FirEmptyExpressionBlock") {
            noSource()
            defaultEmptyList("statements", withGetter = true)
            defaultEmptyList("annotations", withGetter = true)
            publicImplementation()
            defaultNull("coneTypeOrNull")
            default("isUnitCoerced") {
                value = "false"
                withGetter = true
            }
        }

        impl(lazyBlock) {
            val error = """error("FirLazyBlock should be calculated before accessing")"""
            default("source") {
                value = error
                withGetter = true
            }
            default("statements") {
                value = error
                withGetter = true
            }
            default("annotations") {
                value = error
                withGetter = true
            }
            default("coneTypeOrNull") {
                value = error
                withGetter = true
            }
            default("isUnitCoerced") {
                value = error
                withGetter = true
            }
        }

        impl(legacyRawContractDescription) {

        }

        impl(lazyContractDescription) {
            val error = """error("FirLazyContractDescription should be calculated before accessing")"""
            default("source") {
                value = error
                withGetter = true
            }

            default("contractCall") {
                value = error
                withGetter = true
            }

            default("diagnostic") {
                value = error
                withGetter = true
            }
        }

        impl(errorLoop) {
            default("block", "FirEmptyExpressionBlock()")
            default("condition", "FirErrorExpressionImpl(source, MutableOrEmptyList.empty(), ConeUnreportedDuplicateDiagnostic(diagnostic), null, null)")
            additionalImports(emptyExpressionBlock, coneUnreportedDuplicateDiagnosticType)
        }

        impl(expression, "FirExpressionStub") {
            publicImplementation()
        }

        impl(lazyExpression) {
            val error = """error("FirLazyExpression should be calculated before accessing")"""
            default("coneTypeOrNull") {
                value = error
                withGetter = true
            }
            default("annotations") {
                value = error
                withGetter = true
            }
        }

        impl(functionCall) {
            kind = OpenClass
        }

        impl(implicitInvokeCall) {
            default("origin", "FirFunctionCallOrigin.Operator")
        }

        impl(componentCall) {
            default("origin", "FirFunctionCallOrigin.Operator")
        }

        impl(propertyAccessExpression) {
            publicImplementation()
        }

        impl(getClassCall) {
            default("argument") {
                value = "argumentList.arguments.first()"
                withGetter = true
            }
        }

        noImpl(errorTypeRef)

        impl(property) {
            default("isVal") {
                value = "!isVar"
                withGetter = true
            }

            additionalImports(backingFieldSymbolType, delegateFieldSymbolType)
        }

        impl(errorProperty) {
            defaultTrue("isVal", withGetter = true)
            defaultFalse("isVar", withGetter = true)

            defaultNull(
                "receiverParameter",
                "delegate",
                "getter", "setter",
                withGetter = true
            )
            default("returnTypeRef", "FirErrorTypeRefImpl(source, MutableOrEmptyList.empty(), null, null, diagnostic)")
            additionalImports(errorTypeRefImplType)
        }

        impl(field) {
            default("isVal") {
                value = "!isVar"
                withGetter = true
            }
            default("hasConstantInitializer") {
                value = "status.isConst"
                withGetter = true
            }
            publicImplementation()

            defaultNull("receiverParameter", "delegate", "getter", "setter", "containerSource", "backingField", withGetter = true)
            defaultEmptyList("contextParameters", "typeParameters", withGetter = true)
        }

        impl(enumEntry) {
            defaultTrue("isVal", withGetter = true)
            defaultFalse("isVar", withGetter = true)
            defaultNull(
                "receiverParameter", "delegate", "getter", "setter", "containerSource", "dispatchReceiverType", "backingField",
                withGetter = true
            )
            defaultEmptyList("contextParameters", "typeParameters", withGetter = true)
        }

        impl(namedArgumentExpression) {
            default("coneTypeOrNull") {
                delegate = "expression"
            }
        }

        impl(spreadArgumentExpression) {
            default("isSpread") {
                value = "true"
                withGetter = true
            }
            default("coneTypeOrNull") {
                delegate = "expression"
            }
        }

        impl(comparisonExpression) {
            default("coneTypeOrNull", "StandardClassIds.Boolean.constructClassLikeType()")
            additionalImports(standardClassIdsType, constructClassLikeTypeImport)
        }

        impl(typeOperatorCall) {
            defaultFalse("argFromStubType")
        }

        impl(augmentedAssignment)

        impl(incrementDecrementExpression)

        impl(equalityOperatorCall) {
            default("coneTypeOrNull", "StandardClassIds.Boolean.constructClassLikeType()")
            additionalImports(standardClassIdsType, constructClassLikeTypeImport)
        }

        impl(whenBranch, "FirRegularWhenBranch") {
            defaultFalse("hasGuard", withGetter = true)
        }
        impl(whenBranch, "FirGuardedWhenBranch") {
            defaultTrue("hasGuard", withGetter = true)
        }

        impl(resolvedQualifier) {
            isMutable("packageFqName", "relativeClassFqName", "isNullableLHSForCallableReference")
            defaultClassIdFromRelativeClassName()
        }

        impl(resolvedReifiedParameterReference)

        impl(returnExpression) {
            defaultBuiltInType("Nothing")
            additionalImports(implicitNothingTypeRefType)
        }

        impl(stringConcatenationCall) {
            defaultBuiltInType("String")
            additionalImports(implicitStringTypeRefType)
        }

        impl(throwExpression) {
            defaultBuiltInType("Nothing")
            additionalImports(implicitNothingTypeRefType)
        }

        impl(thisReceiverExpression) {
            defaultNull("explicitReceiver", "dispatchReceiver", "extensionReceiver", withGetter = true)
            defaultEmptyList("contextArguments", withGetter = true)
        }

        impl(superReceiverExpression) {
            defaultNull("explicitReceiver", "extensionReceiver", withGetter = true)
            defaultEmptyList("contextArguments", withGetter = true)
        }

        impl(expression, "FirUnitExpression") {
            kDoc(
                """
                A special kind of expression that can only appear inside [${returnExpression.typeName}].
                It denotes an empty `return` expression, which is different from explicit `return Unit`.
                """.trimIndent()
            )
            defaultBuiltInType("Unit")
            additionalImports(returnExpression)
            publicImplementation()
        }

        impl(anonymousFunction) {
            defaultNull("containerSource", withGetter = true)
        }

        noImpl(anonymousFunctionExpression)

        impl(propertyAccessor) {
            defaultNull("receiverParameter", "containerSource", withGetter = true)
            defaultEmptyList("contextParameters", "typeParameters", withGetter = true)
            default("isSetter") {
                value = "!isGetter"
                withGetter = true
            }
            additionalImports(modalityType)
            kind = OpenClass
        }

        impl(backingField) {
            kind = OpenClass
            defaultNull(
                "receiverParameter", "delegate", "getter", "setter", "backingField", "containerSource",
                withGetter = true
            )

            default("dispatchReceiverType", "propertySymbol.dispatchReceiverType", withGetter = true)

            defaultEmptyList(
                "contextParameters", "typeParameters",
                withGetter = true
            )
        }

        impl(whenSubjectExpression) {
            defaultNull("explicitReceiver", "dispatchReceiver", "extensionReceiver", withGetter = true)
            defaultEmptyList("contextArguments", withGetter = true)
            defaultEmptyList("typeArguments", withGetter = true)
        }

        impl(desugaredAssignmentValueReferenceExpression) {
            additionalImports(expression)
            default("coneTypeOrNull") {
                value = "expressionRef.value.coneTypeOrNull"
                withGetter = true
            }
        }

        impl(wrappedDelegateExpression) {
            default("coneTypeOrNull") {
                delegate = "expression"
            }
        }

        impl(enumEntryDeserializedAccessExpression) {
            noSource()
            default("coneTypeOrNull") {
                value = "enumClassId.toLookupTag().constructClassType()"
                additionalImports(toLookupTagImport, constructClassTypeImport)
            }
        }

        impl(smartCastExpression) {
            default("isStable") {
                value = "smartcastStability == SmartcastStability.STABLE_VALUE"
                withGetter = true
            }
            default("source") {
                value = "originalExpression.source?.fakeElement(KtFakeSourceElementKind.SmartCastExpression)"
                withGetter = true
            }
            additionalImports(fakeElementImport, fakeSourceElementKindImport)
        }

        impl(resolvedNamedReference)

        impl(resolvedNamedReference, "FirPropertyFromParameterResolvedNamedReference") {
            publicImplementation()
        }

        impl(resolvedErrorReference)

        impl(resolvedCallableReference)

        impl(namedReference, "FirSimpleNamedReference") {
            publicImplementation()
        }

        noImpl(namedReferenceWithCandidateBase)

        impl(delegateFieldReference) {
            default("name") {
                value = "Name.identifier(\"\\\$delegate\")"
                withGetter = true
            }
        }

        impl(backingFieldReference) {
            default("name") {
                value = "Name.identifier(\"\\\$field\")"
                withGetter = true
            }
        }

        impl(thisReference, "FirExplicitThisReference") {
            default("boundSymbol") {
                value = "null"
                isMutable = true
            }
            defaultFalse("isImplicit")
        }

        impl(thisReference, "FirImplicitThisReference") {
            noSource()
            default("labelName") {
                value = "null"
                withGetter = true
            }
            default("boundSymbol") {
                isMutable = false
            }
            defaultTrue("isImplicit")
        }

        impl(superReference, "FirExplicitSuperReference")

        noImpl(controlFlowGraphReference)

        impl(resolvedTypeRef) {
            publicImplementation()
            defaultFalse("customRenderer", withGetter = true)
        }

        impl(errorExpression) {
            default("coneTypeOrNull", "expression?.coneTypeOrNull ?: ConeErrorType(ConeUnreportedDuplicateDiagnostic(diagnostic))", withGetter = true)
            additionalImports(coneErrorTypeType, coneUnreportedDuplicateDiagnosticType)
        }

        impl(qualifiedErrorAccessExpression) {
            default("coneTypeOrNull", "ConeErrorType(ConeUnreportedDuplicateDiagnostic(diagnostic))")
            additionalImports(coneErrorTypeType, coneUnreportedDuplicateDiagnosticType)
        }

        impl(errorFunction) {
            defaultNull("receiverParameter", "body", withGetter = true)
            default("returnTypeRef", "FirErrorTypeRefImpl(null, MutableOrEmptyList.empty(), null, null, diagnostic)")
            additionalImports(errorTypeRefImplType)
        }

        impl(functionTypeRef) {
            defaultFalse("customRenderer", withGetter = true)
        }
        impl(dynamicTypeRef) {
            defaultFalse("customRenderer", withGetter = true)
        }
        impl(intersectionTypeRef) {
            defaultFalse("customRenderer", withGetter = true)
        }
        noImpl(implicitTypeRef)

        impl(reference, "FirStubReference") {
            default("source") {
                value = "null"
                withGetter = true
            }
            kind = Object
        }

        impl(errorNamedReference)

        impl(breakExpression) {
            defaultBuiltInType("Nothing")
            additionalImports(implicitNothingTypeRefType)
        }

        impl(continueExpression) {
            defaultBuiltInType("Nothing")
            additionalImports(implicitNothingTypeRefType)
        }

        fun AbstractImplementationConfigurator<Implementation, Element, Field>.ImplementationContext.configureCommonValueParameter() {
            defaultTrue("isVal", withGetter = true)
            defaultFalse("isVar", withGetter = true)
            defaultNull(
                "getter", "setter", "initializer", "delegate", "receiverParameter", "dispatchReceiverType", "backingField", "containerSource",
                withGetter = true
            )
            defaultEmptyList("contextParameters", withGetter = true)
        }

        impl(valueParameter) {
            configureCommonValueParameter()
        }

        impl(valueParameter, "FirDefaultSetterValueParameter") {
            configureCommonValueParameter()
            default("name", "Name.identifier(\"value\")")
            defaultNull("defaultValue", "initializer", "delegate", withGetter = true)
            defaultFalse("isCrossinline", "isNoinline", "isVararg", "isVar", withGetter = true)
            default("valueParameterKind", "FirValueParameterKind.Regular", withGetter = true)
        }

        impl(simpleFunction)

        impl(safeCallExpression) {
            additionalImports(checkedSafeCallSubject)
        }

        impl(checkedSafeCallSubject) {
            additionalImports(expression)
        }

        impl(resolvedQualifier) {
            // Initialize the value to true if only the companion object is present. This makes a standalone class reference expression
            // correctly resolve to the companion object. For example
            // ```
            // class A {
            //   companion object
            // }
            //
            // val companionOfA = A // This standalone class reference `A` here should resolve to the companion object.
            // ```
            //
            // If this `FirResolvedQualifier` is a receiver expression of some other qualified access, the value is updated in
            // `FirCallResolver` according to the resolution result.
            default("resolvedToCompanionObject", "(symbol?.fir as? FirRegularClass)?.companionObjectSymbol != null")
            additionalImports(regularClass)
        }

        impl(errorResolvedQualifier) {
            defaultFalse("resolvedToCompanionObject", withGetter = true)
            defaultClassIdFromRelativeClassName()
        }

        noImpl(userTypeRef)

        noImpl(annotationArgumentMapping)

        impl(contractElementDeclaration)

        val implementationsWithoutStatusAndTypeParameters = listOf(
            "FirValueParameterImpl",
            "FirDefaultSetterValueParameter",
            "FirErrorPropertyImpl",
            "FirErrorFunctionImpl"
        )

        configureFieldInAllImplementations(
            "status",
            implementationPredicate = { it.typeName in implementationsWithoutStatusAndTypeParameters }
        ) {
            default(it, "FirResolvedDeclarationStatusImpl.DEFAULT_STATUS_FOR_STATUSLESS_DECLARATIONS")
            additionalImports(resolvedDeclarationStatusImplType)
        }

        configureFieldInAllImplementations(
            "typeParameters",
            implementationPredicate = { it.typeName in implementationsWithoutStatusAndTypeParameters }
        ) {
            defaultEmptyList(it, withGetter = true)
            additionalImports(resolvedDeclarationStatusImplType)
        }
    }

    override fun configureAllImplementations(model: Model) {
        configureFieldInAllImplementations(
            fieldName = "controlFlowGraphReference",
            implementationPredicate = { it.typeName != "FirAnonymousFunctionImpl" }
        ) {
            defaultNull(it)
        }

        val implementationWithConfigurableTypeRef = listOf(
            "FirTypeProjectionWithVarianceImpl",
            "FirCallableReferenceAccessImpl",
            "FirThisReceiverExpressionImpl",
            "FirPropertyAccessExpressionImpl",
            "FirFunctionCallImpl",
            "FirAnonymousFunctionImpl",
            "FirWhenExpressionImpl",
            "FirTryExpressionImpl",
            "FirCheckNotNullCallImpl",
            "FirResolvedQualifierImpl",
            "FirResolvedReifiedParameterReferenceImpl",
            "FirExpressionStub",
            "FirVarargArgumentsExpressionImpl",
            "FirSafeCallExpressionImpl",
            "FirCheckedSafeCallSubjectImpl",
            "FirArrayLiteralImpl",
            "FirIntegerLiteralOperatorCallImpl",
            "FirReceiverParameterImpl",
            "FirClassReferenceExpressionImpl",
            "FirGetClassCallImpl",
            "FirSmartCastExpressionImpl",
            "FirInaccessibleReceiverExpressionImpl"
        )
        configureFieldInAllImplementations(
            fieldName = "typeRef",
            implementationPredicate = { it.typeName !in implementationWithConfigurableTypeRef },
            fieldPredicate = { it.implementationDefaultStrategy!!.defaultValue != null }
        ) {
            default(it, "FirImplicitTypeRefImplWithoutSource")
            additionalImports(firImplicitTypeWithoutSourceType)
        }

        configureFieldInAllImplementations(
            fieldName = "lValueTypeRef",
            implementationPredicate = { it.typeName in "FirVariableAssignmentImpl" },
            fieldPredicate = { it.implementationDefaultStrategy!!.defaultValue != null }
        ) {
            default(it, "FirImplicitTypeRefImplWithoutSource")
            additionalImports(firImplicitTypeWithoutSourceType)
        }

        configureAllImplementations(
            implementationPredicate = {
                fun hasDeclarationSupertype(element: Element): Boolean {
                    if (element == declaration) return true
                    return element.allParents.any { hasDeclarationSupertype(it) }
                }
                hasDeclarationSupertype(it.element)
            }
        ) {
            optInToInternals()
        }
    }

    private fun ImplementationContext.defaultClassIdFromRelativeClassName() {
        default("classId") {
            value = """
                |relativeClassFqName?.let {
                |    ClassId(packageFqName, it, isLocal = false)
                |}
                """.trimMargin()
            withGetter = true
        }
    }
}
