// RUN_PIPELINE_TILL: FRONTEND
fun test1() {
    if (<!UNRESOLVED_REFERENCE!>rr<!>) {
        if (<!UNRESOLVED_REFERENCE!>l<!>) {
            <!UNRESOLVED_REFERENCE!>a<!>.q()
        }
        else {
            <!UNRESOLVED_REFERENCE!>a<!>.w()
        }
    }
    else {
        if (<!UNRESOLVED_REFERENCE!>n<!>) {
            <!UNRESOLVED_REFERENCE!>a<!>.t()
        }
        else {
            <!UNRESOLVED_REFERENCE!>a<!>.u()
        }
    }
}

fun test2(l: List<<!UNRESOLVED_REFERENCE!>AA<!>>) {
    l.<!UNRESOLVED_REFERENCE!>map<!> {
        <!UNRESOLVED_REFERENCE!>it<!>!!
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, functionDeclaration, ifExpression, lambdaLiteral */
