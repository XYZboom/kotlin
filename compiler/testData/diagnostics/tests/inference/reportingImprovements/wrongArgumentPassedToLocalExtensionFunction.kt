// RUN_PIPELINE_TILL: FRONTEND
// WITH_STDLIB

fun Runnable.test(f: Runnable.(Int) -> Unit) {
    f(<!TYPE_MISMATCH!>""<!>)
}

fun test(f: Runnable.(Int) -> Unit, runnable: Runnable) {
    with (runnable) {
        f(<!TYPE_MISMATCH!>""<!>)
    }
}

fun Int.test(f: String.(Int) -> Unit) {
    f("", 0)
    f(<!NO_VALUE_FOR_PARAMETER!>"")<!>
    with("") {
        f(0)
        f(<!CONSTANT_EXPECTED_TYPE_MISMATCH!>0.0<!>)
    }
}

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, functionalType, integerLiteral, lambdaLiteral,
stringLiteral, typeWithExtension */
