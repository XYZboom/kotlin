// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// CHECK_TYPE

fun <T> bar(f: () -> T) : T = f()

fun test(map: MutableMap<Int, Int>) {
    val r = bar {
        map[1] = 2
    }
    r checkType { _<Unit>() }
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, funWithExtensionReceiver, functionDeclaration, functionalType,
infix, integerLiteral, lambdaLiteral, localProperty, nullableType, propertyDeclaration, typeParameter, typeWithExtension */
