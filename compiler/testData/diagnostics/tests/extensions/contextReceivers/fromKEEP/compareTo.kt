// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -CONTEXT_RECEIVERS_DEPRECATED
// FIR_IDENTICAL
// LANGUAGE: +ContextReceivers

data class Pair<A, B>(val first: A, val second: B)

context(Comparator<T>)
infix operator fun <T> T.compareTo(other: T) = compare(this, other)

context(Comparator<T>)
val <T> Pair<T, T>.max get() = if (first > second) first else second

fun test() {
    val comparator = Comparator<String> { a, b ->
        if (a == null || b == null) 0 else a.length.compareTo(b.length)
    }
    with(comparator) {
        Pair("OK", "fail").max
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, comparisonExpression, data, disjunctionExpression, equalityExpression,
flexibleType, funWithExtensionReceiver, functionDeclaration, functionDeclarationWithContext, getter, ifExpression, infix,
integerLiteral, lambdaLiteral, localProperty, nullableType, operator, primaryConstructor, propertyDeclaration,
propertyDeclarationWithContext, propertyWithExtensionReceiver, smartcast, stringLiteral, thisExpression, typeParameter */
