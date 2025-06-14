// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
typealias SuspendFn = suspend () -> Unit

val test1: suspend () -> Unit = {}
val test2: suspend Any.() -> Unit = {}
val test3: suspend Any.(Int) -> Int = { k: Int -> k + 1 }
val test4: SuspendFn = {}

/* GENERATED_FIR_TAGS: additiveExpression, functionalType, integerLiteral, lambdaLiteral, propertyDeclaration, suspend,
typeAliasDeclaration, typeWithExtension */
