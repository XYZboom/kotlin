// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// FILE: a.kt

package foo

object Bar {
    fun bar() {}
}

// FILE: b.kt

package baz

import foo.Bar

class C: <!SINGLETON_IN_SUPERTYPE!>Bar<!>

fun test() {
    Bar.bar()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, objectDeclaration */
