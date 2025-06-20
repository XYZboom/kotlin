// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

// FILE: A.java
public class A {
    public java.util.List<? extends CharSequence> foo() {}
}

// FILE: main.kt

fun foo2(x: A, y: MutableList<out CharSequence>) {
    x.foo().isEmpty()
    x.foo().get(0) checkType { _<CharSequence>() }
    x.foo().iterator() checkType { _<MutableIterator<CharSequence>>() }

    y.isEmpty()
    y.get(0) checkType { _<CharSequence>() }
    y.iterator() checkType { _<MutableIterator<CharSequence>>() }
}

/* GENERATED_FIR_TAGS: capturedType, classDeclaration, flexibleType, funWithExtensionReceiver, functionDeclaration,
functionalType, infix, integerLiteral, javaFunction, javaType, lambdaLiteral, nullableType, outProjection, typeParameter,
typeWithExtension */
