// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// FILE: A.java
import java.io.Closeable;

public class A {
    public static void foo(Runnable r) {
    }

    public static void foo(Closeable c) {
    }
}

// FILE: test.kt

fun main() {
    A.<!OVERLOAD_RESOLUTION_AMBIGUITY!>foo<!> { "Hello!" }
    A.foo(Runnable { "Hello!" })
}

/* GENERATED_FIR_TAGS: functionDeclaration, javaFunction, lambdaLiteral, stringLiteral */
