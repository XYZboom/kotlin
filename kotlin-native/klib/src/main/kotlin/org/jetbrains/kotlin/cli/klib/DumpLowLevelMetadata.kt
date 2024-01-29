/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.klib

import kotlinx.metadata.*
import kotlinx.metadata.klib.*
import org.jetbrains.kotlin.utils.Printer
import kotlin.contracts.ExperimentalContracts

@OptIn(ExperimentalContextReceivers::class, ExperimentalContracts::class)
internal class DumpLowLevelMetadata(output: Appendable) {
    private val printer = Printer(output)

    fun dumpModule(module: KlibModuleMetadata) = dumpEntity("MODULE", module.name) {
        dumpAnnotations(hasAnnotations = null, module.annotations)
        module.fragments.forEach { fragment ->
            dumpEntity("FRAGMENT", fragment.fqName?.ifEmpty { "<root>" }.orEmpty()) {
                dumpList("sourceFiles", fragment.moduleFragmentFiles) { it.name }
                dumpList("classNames", fragment.className)
                fragment.classes.forEach(::dumpClass)

                val pkg = fragment.pkg ?: return@forEach
                dumpEntity("PACKAGE", pkg.fqName?.ifEmpty { "<root>" }.orEmpty()) {
                    pkg.properties.forEach(::dumpProperty)
                    pkg.functions.forEach(::dumpFunction)
                    pkg.typeAliases.forEach(::dumpTypeAlias)
                }
            }
        }
    }

    private fun dumpAnnotations(hasAnnotations: Boolean?, annotations: List<KmAnnotation>) {
        dumpValue("hasAnnotations", hasAnnotations)
        annotations.forEach { annotation ->
            dumpEntity("ANNOTATION", annotation.className) {
                annotation.arguments.forEach { (name, argument) -> dumpValue(name, argument) }
            }
        }
    }

    private fun dumpClass(clazz: KmClass) = dumpEntity("CLASS", clazz.name) {
        dumpAnnotations(clazz.hasAnnotations, clazz.annotations)
        dumpVisibility(clazz.visibility)
        dumpModality(clazz.modality)
        dumpKind(clazz.kind)
        dumpValue("isFunInterface", clazz.isFunInterface)
        dumpValue("isData", clazz.isData)
        dumpValue("isValue", clazz.isValue)
        dumpValue("valueClassUnderlyingPropertyName", clazz.inlineClassUnderlyingPropertyName)
        dumpType("valueClassUnderlyingType", clazz.inlineClassUnderlyingType)
        dumpValue("isInner", clazz.isInner)
        dumpList("nestedClasses", clazz.nestedClasses)
        dumpList("sealedSubclasses", clazz.sealedSubclasses)
        dumpValue("companionObject", clazz.companionObject)
        dumpValue("hasEnumEntries", clazz.hasEnumEntries)
        dumpList("enumEntries", clazz.enumEntries)
        dumpComplex("klibEnumEntries", clazz.klibEnumEntries, ::dumpEnumEntry)
        dumpValue("isExternal", clazz.isExternal)
        dumpValue("isExpect", clazz.isExpect)
        dumpUniqId(clazz.uniqId)
        dumpSourceFile(clazz.file)
        dumpVersionRequirements(clazz.versionRequirements)
        dumpTypeParameters(clazz.typeParameters)
        dumpTypes("supertypes", clazz.supertypes)
        dumpTypes("contextReceiverTypes", clazz.contextReceiverTypes)
        clazz.constructors.forEach(::dumpConstructor)
        clazz.properties.forEach(::dumpProperty)
        clazz.functions.forEach(::dumpFunction)
        clazz.typeAliases.forEach(::dumpTypeAlias)
    }

    private fun dumpConstructor(constructor: KmConstructor) = dumpEntity("CONSTRUCTOR", "") {
        dumpAnnotations(constructor.hasAnnotations, constructor.annotations)
        dumpVisibility(constructor.visibility)
        dumpValue("isSecondary", constructor.isSecondary)
        dumpValue("hasNonStableParameterNames", constructor.hasNonStableParameterNames)
        dumpUniqId(constructor.uniqId)
        dumpVersionRequirements(constructor.versionRequirements)
        dumpValueParameters(constructor.valueParameters)
    }

    private fun dumpFunction(function: KmFunction) = dumpEntity("FUNCTION", function.name) {
        dumpAnnotations(function.hasAnnotations, function.annotations)
        dumpVisibility(function.visibility)
        dumpModality(function.modality)
        dumpKind(function.kind)
        dumpValue("isOperator", function.isOperator)
        dumpValue("isInfix", function.isInfix)
        dumpValue("isInline", function.isInline)
        dumpValue("isTailrec", function.isTailrec)
        dumpValue("isExternal", function.isExternal)
        dumpValue("isExpect", function.isExpect)
        dumpValue("isSuspend", function.isSuspend)
        dumpValue("hasNonStableParameterNames", function.hasNonStableParameterNames)
        dumpUniqId(function.uniqId)
        dumpSourceFile(function.file)
        dumpVersionRequirements(function.versionRequirements)
        dumpTypeParameters(function.typeParameters)
        dumpTypes("contextReceiverTypes", function.contextReceiverTypes)
        dumpType("receiverParameterType", function.receiverParameterType)
        dumpType("returnType", function.returnType)
        dumpValueParameters(function.valueParameters)

        function.contract?.let { contract ->
            dumpEntity("CONTRACT", "") {
                contract.effects.forEach { effect ->
                    dumpEntity("EFFECT", effect.type.name.lowercase()) {
                        dumpValue("invocationKind", effect.invocationKind?.name?.lowercase())
                        dumpComplex("constructorArguments", effect.constructorArguments, ::dumpEffectExpression)
                        dumpComplex("conclusion", listOfNotNull(effect.conclusion), ::dumpEffectExpression)
                    }
                }
            }
        }
    }

    private fun dumpEffectExpression(effectExpression: KmEffectExpression) = dumpEntity("EFFECT EXPRESSION", "") {
        dumpValue("parameterIndex", effectExpression.parameterIndex)
        dumpValue("constantValue", effectExpression.constantValue)
        dumpType("isInstanceType", effectExpression.isInstanceType)
        dumpComplex("andArguments", effectExpression.andArguments, ::dumpEffectExpression)
        dumpComplex("orArguments", effectExpression.orArguments, ::dumpEffectExpression)
        dumpValue("isNegated", effectExpression.isNegated)
        dumpValue("isNullCheckPredicate", effectExpression.isNullCheckPredicate)
    }

    private fun dumpProperty(property: KmProperty) = dumpEntity("PROPERTY", property.name) {
        dumpAnnotations(property.hasAnnotations, property.annotations)
        dumpVisibility(property.visibility)
        dumpModality(property.modality)
        dumpKind(property.kind)
        dumpValue("isVar", property.isVar)
        dumpValue("isConst", property.isConst)
        dumpValue("hasConstant", property.hasConstant)
        dumpValue("compileTimeValue", property.compileTimeValue)
        dumpValue("isLateInit", property.isLateinit)
        dumpValue("isExternal", property.isExternal)
        dumpValue("isDelegated", property.isDelegated)
        dumpValue("isExpect", property.isExpect)
        dumpUniqId(property.uniqId)
        dumpValue("file", property.file)
        dumpVersionRequirements(property.versionRequirements)
        dumpTypeParameters(property.typeParameters)
        dumpTypes("contextReceiverTypes", property.contextReceiverTypes)
        dumpType("receiverParameterType", property.receiverParameterType)
        dumpType("returnType", property.returnType)
        dumpEntity("GETTER", "") {
            val getter = property.getter
            dumpAnnotations(getter.hasAnnotations, property.getterAnnotations)
            dumpVisibility(getter.visibility)
            dumpModality(getter.modality)
            dumpValue("isInline", getter.isInline)
            dumpValue("isNotDefault", getter.isNotDefault)
            dumpValue("isExternal", getter.isExternal)
        }
        val setter = property.setter
        if (setter != null || property.setterAnnotations.isNotEmpty() || property.setterParameter != null) {
            dumpEntity("SETTER", "") {
                dumpAnnotations(setter?.hasAnnotations, property.setterAnnotations)
                dumpVisibility(setter?.visibility)
                dumpModality(setter?.modality)
                dumpValue("isInline", setter?.isInline)
                dumpValue("isNotDefault", setter?.isNotDefault)
                dumpValue("isExternal", setter?.isExternal)
                dumpValueParameters(listOfNotNull(property.setterParameter))
            }
        }
    }

    private fun dumpTypeAlias(typeAlias: KmTypeAlias) = dumpEntity("TYPEALIAS", typeAlias.name) {
        dumpAnnotations(typeAlias.hasAnnotations, typeAlias.annotations)
        dumpVisibility(typeAlias.visibility)
        dumpUniqId(typeAlias.uniqId)
        dumpVersionRequirements(typeAlias.versionRequirements)
        dumpTypeParameters(typeAlias.typeParameters)
        dumpType("underlyingType", typeAlias.underlyingType)
        dumpType("expandedType", typeAlias.expandedType)
    }

    private fun dumpTypeParameters(typeParameters: List<KmTypeParameter>) {
        dumpComplex("typeParameters", typeParameters) { typeParameter ->
            dumpEntity("TYPE_PARAMETER", typeParameter.name) {
                dumpAnnotations(hasAnnotations = null, typeParameter.annotations)
                dumpValue("id", typeParameter.id)
                dumpValue("variance", typeParameter.variance.name.lowercase())
                dumpValue("isReified", typeParameter.isReified)
                dumpUniqId(typeParameter.uniqId)
                dumpTypes("upperBounds", typeParameter.upperBounds)
            }
        }
    }

    private fun dumpType(name: String, type: KmType?): Unit = dumpTypes(name, listOfNotNull(type))
    private fun dumpTypes(name: String, types: List<KmType>) = dumpComplex(name, types, ::dumpType)

    private fun dumpType(type: KmType) = dumpEntity("TYPE", "") {
        dumpAnnotations(hasAnnotations = null, type.annotations)
        when (val classifier = type.classifier) {
            is KmClassifier.Class -> dumpValue("class", classifier.name)
            is KmClassifier.TypeAlias -> dumpValue("typeAlias", classifier.name)
            is KmClassifier.TypeParameter -> dumpValue("typeParameter", classifier.id)
        }
        dumpValue("isNullable", type.isNullable)
        dumpValue("isDefinitelyNonNull", type.isDefinitelyNonNull)
        dumpValue("isSuspend", type.isSuspend)
        dumpTypeArguments(type.arguments)
        dumpType("outerType", type.outerType)
        dumpType("abbreviatedType", type.abbreviatedType)
        type.flexibleTypeUpperBound?.let { flexibleTypeUpperBound ->
            dumpValue("flexibleTypeUpperBound.typeFlexibilityId", flexibleTypeUpperBound.typeFlexibilityId)
            dumpType("flexibleTypeUpperBound.type", flexibleTypeUpperBound.type)
        }
    }

    private fun dumpTypeArguments(typeArguments: List<KmTypeProjection>) {
        dumpComplex("typeArguments", typeArguments) { typeArgument ->
            if (typeArgument == KmTypeProjection.STAR)
                dumpEntity("TYPE_ARGUMENT", "*") {}
            else
                dumpEntity("TYPE_ARGUMENT", "") {
                    dumpType("type", typeArgument.type)
                    dumpValue("variance", typeArgument.variance?.name?.lowercase())
                }
        }
    }

    private fun dumpValueParameters(valueParameters: List<KmValueParameter>) {
        dumpComplex("valueParameters", valueParameters) { valueParameter ->
            dumpEntity("VALUE_PARAMETER", valueParameter.name) {
                dumpAnnotations(valueParameter.hasAnnotations, valueParameter.annotations)
                dumpValue("declaresDefaultValue", valueParameter.declaresDefaultValue)
                dumpValue("isCrossinline", valueParameter.isCrossinline)
                dumpValue("isNoinline", valueParameter.isNoinline)
                dumpType("type", valueParameter.type)
                dumpType("varargElementType", valueParameter.varargElementType)
            }
        }
    }

    private fun dumpEnumEntry(enumEntry: KlibEnumEntry) = dumpEntity(enumEntry.name, "") {
        dumpAnnotations(hasAnnotations = null, enumEntry.annotations)
        dumpUniqId(enumEntry.uniqId)
        dumpValue("ordinal", enumEntry.ordinal)
    }

    private fun dumpVisibility(visibility: Visibility?) = dumpValue("visibility", visibility?.name?.lowercase())
    private fun dumpModality(modality: Modality?) = dumpValue("modality", modality?.name?.lowercase())
    private fun <K : Enum<K>> dumpKind(kind: K) = dumpValue("kind", kind.name.lowercase())
    private fun dumpUniqId(uniqId: UniqId?) = dumpValue("uniqId", uniqId?.index)
    private fun dumpSourceFile(file: KlibSourceFile?) = dumpValue("sourceFile", file?.name)

    private fun dumpVersionRequirements(versionRequirements: List<KmVersionRequirement>) {
        versionRequirements.forEach { versionRequirement ->
            dumpEntity("VERSION REQUIREMENT", versionRequirement.version.toString()) {
                dumpValue("kind", versionRequirement.kind)
                dumpValue("level", versionRequirement.level)
                dumpValue("errorCode", versionRequirement.errorCode)
                dumpValue("message", versionRequirement.message)
            }
        }
    }

    private fun dumpValue(name: String, value: Any?) {
        if (value != null) {
            printer.println("$name: $value")
        }
    }

    private fun dumpList(name: String, values: List<String>) = dumpList(name, values) { it }

    private inline fun <T : Any> dumpList(name: String, values: List<T>, transform: (T) -> String) {
        if (values.isNotEmpty()) {
            printer.println("$name:")
            indented { values.forEach { printer.println(transform(it)) } }
        }
    }

    private inline fun dumpEntity(type: String, name: String, members: () -> Unit) {
        printer.println(if (name.isNotEmpty()) "$type $name" else type)
        indented(members)
    }

    private inline fun <T : Any> dumpComplex(name: String, members: List<T>, dumpMember: (T) -> Unit) {
        if (members.isNotEmpty()) {
            printer.println("$name:")
            indented { members.forEach(dumpMember) }
        }
    }

    private inline fun <T> indented(block: () -> T): T {
        printer.pushIndent()
        try {
            return block()
        } finally {
            printer.popIndent()
        }
    }
}
