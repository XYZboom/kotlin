// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +WarnAboutNonExhaustiveWhenOnAlgebraicTypes
// DIAGNOSTICS: -UNUSED_VALUE

fun foo(f: Boolean): Int {
    val i: Int
    <!NO_ELSE_IN_WHEN!>when<!> (f) {
        true -> i = 1
    }
    <!VAL_REASSIGNMENT!>i<!> = 3
    return i
}

/* GENERATED_FIR_TAGS: assignment, equalityExpression, functionDeclaration, integerLiteral, localProperty,
propertyDeclaration, whenExpression, whenWithSubject */
