// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: a/Statics.java

package a;

public class Statics {
    public static void foo(Runnable r) {}
}

// FILE: test.kt

package b

import a.Statics.*

fun test() {
    foo {}
}

/* GENERATED_FIR_TAGS: functionDeclaration, javaFunction, lambdaLiteral, samConversion */
