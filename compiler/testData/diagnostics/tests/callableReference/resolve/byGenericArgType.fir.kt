// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER

fun <T> ofType(x: T): T = x

fun foo() {}
fun foo(s: String) {}

val x1 = ofType<() -> Unit>(::foo)
val x2 = ofType<(String) -> Unit>(::foo)
val x3 = ofType<(Int) -> Unit>(::<!NONE_APPLICABLE!>foo<!>)

/* GENERATED_FIR_TAGS: callableReference, functionDeclaration, functionalType, nullableType, propertyDeclaration,
typeParameter */
