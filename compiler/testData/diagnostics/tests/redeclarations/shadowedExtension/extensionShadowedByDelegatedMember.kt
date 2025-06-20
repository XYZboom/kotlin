// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

interface IBase {
    fun foo()
    val bar: Int
}

object Impl : IBase {
    override fun foo() {}
    override val bar: Int get() = 42
}

object Test : IBase by Impl

fun Test.<!EXTENSION_SHADOWED_BY_MEMBER!>foo<!>() {}
val Test.<!EXTENSION_SHADOWED_BY_MEMBER!>bar<!>: Int get() = 42

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, getter, inheritanceDelegation, integerLiteral,
interfaceDeclaration, objectDeclaration, override, propertyDeclaration, propertyWithExtensionReceiver */
