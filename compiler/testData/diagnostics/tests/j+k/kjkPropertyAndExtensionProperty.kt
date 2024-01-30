// FIR_IDENTICAL
// DIAGNOSTICS: -EXTENSION_SHADOWED_BY_MEMBER

// FILE: Java1.java
public class Java1 extends D { }

// FILE: 1.kt
open class D {
    open val D.a: Int
        get() = 1
    val a : Int
        get() = 2
}

class F : Java1()