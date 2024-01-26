/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// a package is omitted to get declarations directly under the module
package kotlin.wasm.internal

import kotlin.reflect.*
import kotlin.reflect.wasm.internal.*

@Suppress("UNUSED_PARAMETER")
private fun getJsClass(typeName: String): ExternalInterfaceType? =
    js("globalThis[typeName]")

@Suppress("UNUSED_PARAMETER")
private fun getJsClassName(jsKlass: ExternalInterfaceType): String? =
    js("jsKlass.name")

@Suppress("UNUSED_PARAMETER")
private fun instanceOf(jsKlass: ExternalInterfaceType, ref: ExternalInterfaceType): Boolean =
    js("ref instanceof jsKlass")

@Suppress("UNUSED_PARAMETER")
private fun getConstructor(obj: ExternalInterfaceType): ExternalInterfaceType? =
    js("obj.constructor")

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> getKClass(typeInfoData: TypeInfoData): KClass<T> {
    if (typeInfoData.isExternal) {
        val jsKlass = getJsClass(typeInfoData.typeName) ?: return ErrorKClass as KClass<T>
        return KExternalClassImpl(jsKlass)
    } else {
        return KClassImpl(typeInfoData)
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> getKClassForObject(obj: Any): KClass<T> {
    if (obj !is JsExternalBox) return getKClass(getTypeInfoTypeDataByPtr(obj.typeInfo))
    val jsKlassName = getConstructor(obj.ref) ?: return ErrorKClass as KClass<T>
    return KExternalClassImpl(jsKlassName)
}

internal class KExternalClassImpl<T : Any>(internal val jsKlass: ExternalInterfaceType) : KClass<T> {
    override val simpleName: String get() = getJsClassName(jsKlass) ?: "null"
    override val qualifiedName: String = simpleName

    override fun isInstance(value: Any?): Boolean =
        value is JsExternalBox && instanceOf(jsKlass, value.ref)

    override fun equals(other: Any?): Boolean =
        other is KExternalClassImpl<*> && jsKlass == other.jsKlass

    override fun hashCode(): Int = simpleName.hashCode()

    override fun toString(): String = "class $simpleName"
}