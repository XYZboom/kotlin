// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL

// FILE: A.java

public class A<T> {
    public void add(T x) {}
    public static class B extends A<Runnable> {}
}

// FILE: test.kt

fun test(x: A.B) {
    x.add { }
}

/* GENERATED_FIR_TAGS: functionDeclaration, javaType, lambdaLiteral, samConversion */
