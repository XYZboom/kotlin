// RUN_PIPELINE_TILL: FRONTEND
fun foo() {}

class C {
    fun bar() {}
    fun err() {}

    class Nested {
        fun test() {
            <!UNRESOLVED_REFERENCE!>err<!>()
        }
    }
}

fun test() {
    val c = C()
    foo()
    c.bar()

    val err = C()
    err.<!UNRESOLVED_REFERENCE!>foo<!>()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, localProperty, nestedClass, propertyDeclaration */
