// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: BACKEND
// MODULE: common
expect class A() : B
expect class C() : B
expect open class B()

// MODULE: intermediate()()(common)
actual class A : B() {
    // "Nothing to override" in metadata compilation. Unfortunately we don't check metadata compilation in diagnostic tests
    <!NOTHING_TO_OVERRIDE{METADATA}!>override<!> fun foo() {}
}
actual class C : B() {
    // Nothing to override in platform compilation.
    fun <!VIRTUAL_MEMBER_HIDDEN!>foo<!>() {}
}

// MODULE: main()()(intermediate)
actual open class B {
    open fun foo() {}
}

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, functionDeclaration, override, primaryConstructor */
