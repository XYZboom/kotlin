// RUN_PIPELINE_TILL: FRONTEND
fun import() {
    <!FUNCTION_CALL_EXPECTED!>import<!> <!UNRESOLVED_REFERENCE!>a<!><!SYNTAX!>.<!><!UNRESOLVED_REFERENCE!>*<!><!SYNTAX!><!>
}

fun composite() {
    val s = 13+<!SYNTAX!>~<!><!UNRESOLVED_REFERENCE!>/<!>12
}

fun html() {
    <!SYNTAX!><<!><!FUNCTION_CALL_EXPECTED!>html<!><!UNRESOLVED_REFERENCE!>><!><!SYNTAX!><<!><!UNRESOLVED_REFERENCE!>/<!><!FUNCTION_CALL_EXPECTED!>html<!>><!SYNTAX!><!>
}

fun html1() {
    <!SYNTAX!><<!><!FUNCTION_CALL_EXPECTED!>html<!><!UNRESOLVED_REFERENCE!>><!><!SYNTAX!><<!><!UNRESOLVED_REFERENCE!>/<!><!FUNCTION_CALL_EXPECTED!>html<!>><!ARGUMENT_TYPE_MISMATCH, FUNCTION_CALL_EXPECTED!>html<!>
}

/* GENERATED_FIR_TAGS: additiveExpression, comparisonExpression, functionDeclaration, integerLiteral, localProperty,
multiplicativeExpression, propertyDeclaration */
