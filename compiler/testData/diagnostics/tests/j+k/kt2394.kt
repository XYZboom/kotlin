// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

//KT-2394 java.lang.Iterable<T> should be visible as kotlin.Iterable<out T>
package d

import checkSubtype

fun foo(iterable: Iterable<Int>, iterator: Iterator<Int>, comparable: Comparable<Any>) {
    checkSubtype<Iterable<Any>>(iterable)
    checkSubtype<Iterator<Any>>(iterator)
    checkSubtype<Comparable<String>>(comparable)
}

fun bar(c: Collection<Int>) {
    checkSubtype<Iterable<Any>>(c)
    checkSubtype<Iterator<Any>>(c.iterator())
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType, infix,
nullableType, typeParameter, typeWithExtension */
