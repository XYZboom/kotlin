// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE
class A

fun test(foo: A.() -> Int, a: A) {
    val b: Int = foo(a)
    val c: Int = (foo)(a)
}

class B {
    val foo: A.() -> Int = null!!

    init {
        val b: Int = foo(A())
    }
}

fun foo(): A.() -> Int {
    val b: Int = foo()(A())
    val c: Int = (foo())(A())

    return null!!
}

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, functionDeclaration, functionalType, init, localProperty,
propertyDeclaration, typeWithExtension */
