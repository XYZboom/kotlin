// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
open class A {
    open external fun foo()
}

class B : A() {
    override fun foo() {
        super.foo()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, external, functionDeclaration, override, superExpression */
