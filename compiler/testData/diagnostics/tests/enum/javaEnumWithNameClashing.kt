// DISABLE_JAVA_FACADE
// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: A.java
public enum A {
    ENTRY,
    ANOTHER;

    public String ENTRY = "";
}

// FILE: test.kt

fun main() {
    val c: A = A.ENTRY
    val c2: String? = c.ENTRY
    val c3: String? = A.ANOTHER.ENTRY
}

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaProperty, javaType, localProperty, nullableType,
propertyDeclaration */
