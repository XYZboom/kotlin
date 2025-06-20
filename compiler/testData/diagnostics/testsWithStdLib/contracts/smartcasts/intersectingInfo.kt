// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +AllowContractsForCustomFunctions +UseReturnsEffect
// OPT_IN: kotlin.contracts.ExperimentalContracts
// DIAGNOSTICS: -INVISIBLE_REFERENCE -INVISIBLE_MEMBER

import kotlin.contracts.*

fun isString(x: Any?): Boolean {
    contract {
        returns(true) implies (x is String)
    }
    return x is String
}


fun notIsString(x: Any?): Boolean {
    contract {
        returns(false) implies (x is String)
    }
    return x !is String
}

fun notIsInt(x: Any?): Boolean {
    contract {
        returns(false) implies (x !is Int)
    }
    return x !is Int
}

fun intersectingInfo(x: Any?, y: Any?) {
    if ((isString(x) && y is String) || (!notIsString(x) && !notIsInt(y))) {
        <!DEBUG_INFO_SMARTCAST!>x<!>.length
        y.<!UNRESOLVED_REFERENCE!>length<!>
        y.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
    }
    else {
        x.<!UNRESOLVED_REFERENCE!>length<!>
        y.<!UNRESOLVED_REFERENCE!>length<!>
        y.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
    }
}

fun intersectingInfo2(x: Any?, y: Any?) {
    // In each arg of "||"-operator presented fact "x is String" which should lead to smartcast.
    // Also there are 3 additional facts: "x is Int", "y is String", "y is Int". One
    // of them is absent in each arg of "||"-operator, so they *shouldn't* lead to smartcast

    if ((isString(x) && !notIsInt(x) && y is String) ||
        (!notIsString(x) && isString(y) && <!USELESS_IS_CHECK!>y is Int<!>) ||
        (x is String && !notIsInt(y) && <!USELESS_IS_CHECK!>x is Int<!>)) {
        <!DEBUG_INFO_SMARTCAST!>x<!>.length
        x.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
        y.<!UNRESOLVED_REFERENCE!>length<!>
        y.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
    }
    x.<!UNRESOLVED_REFERENCE!>length<!>
    x.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
    y.<!UNRESOLVED_REFERENCE!>length<!>
    y.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inc<!>()
}

/* GENERATED_FIR_TAGS: andExpression, contractConditionalEffect, contracts, disjunctionExpression, functionDeclaration,
ifExpression, isExpression, lambdaLiteral, nullableType, smartcast */
