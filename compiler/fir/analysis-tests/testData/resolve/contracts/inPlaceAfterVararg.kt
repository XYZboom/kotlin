// RUN_PIPELINE_TILL: BACKEND
// WITH_STDLIB
// ISSUE: KT-30497
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun example(vararg strings: String, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
}

fun test() {
    val x: String
    example("") {
        x = ""
    }
    x.length
}

/* GENERATED_FIR_TAGS: assignment, classReference, contractCallsEffect, contracts, functionDeclaration, functionalType,
lambdaLiteral, localProperty, propertyDeclaration, stringLiteral, vararg */
