// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
fun box() {
    fun local():Int {
    <!NO_RETURN_IN_FUNCTION_WITH_BLOCK_BODY!>}<!>
}

interface X {
    fun f(): Boolean
}

val m = object : X {
    override fun f(): Boolean {
    <!NO_RETURN_IN_FUNCTION_WITH_BLOCK_BODY!>}<!>
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, functionDeclaration, interfaceDeclaration, localFunction, override,
propertyDeclaration */
