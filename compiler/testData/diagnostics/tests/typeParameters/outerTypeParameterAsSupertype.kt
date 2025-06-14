// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL

package test

interface OuterParam

class A: OuterParam

class Outer<OuterParam> {

    class Nested: OuterParam {
        fun foo(): OuterParam = A()
    }
}

fun main() {
    val c: OuterParam = Outer.Nested().foo()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, localProperty, nestedClass,
nullableType, propertyDeclaration, typeParameter */
