// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

interface A {
    fun foo(): CharSequence?
    fun baz(x: Any) {}
}

interface B {
    fun foo(): String
    fun baz(x: Int): String =""
    fun baz(x: Int, y: Int) {}

    fun foobar(): CharSequence?
}

interface C {
    fun foo(): String
    fun baz(x: Int): String =""
    fun baz(x: Int, y: Int) {}

    fun foobar(): String
}

var x: A = null!!

fun test() {
    x.foo().checkType { _<CharSequence?>() }

    if (x is B && x is C) {
        x.foo().checkType { _<CharSequence?>() }
        x.baz("")
        x.baz(1).checkType { _<Unit>() }
        <!SMARTCAST_IMPOSSIBLE!>x<!>.baz(1, 2)

        <!SMARTCAST_IMPOSSIBLE!>x<!>.foobar().checkType { _<String>() }
    }
}

/* GENERATED_FIR_TAGS: andExpression, checkNotNullCall, classDeclaration, funWithExtensionReceiver, functionDeclaration,
functionalType, ifExpression, infix, integerLiteral, interfaceDeclaration, intersectionType, isExpression, lambdaLiteral,
nullableType, propertyDeclaration, smartcast, stringLiteral, typeParameter, typeWithExtension */
