// RUN_PIPELINE_TILL: BACKEND
//KT-5155 Auto-casts do not work with when

fun foo(s: String?) {
    when {
        s == null -> 1
        s.foo() -> 2
        else -> 3
    }
}

fun String.foo() = true

/* GENERATED_FIR_TAGS: equalityExpression, funWithExtensionReceiver, functionDeclaration, integerLiteral, nullableType,
smartcast, whenExpression */
