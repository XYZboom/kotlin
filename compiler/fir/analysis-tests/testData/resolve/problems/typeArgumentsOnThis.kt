// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-24737

fun x(){
    <!NO_THIS, UNRESOLVED_REFERENCE!>this<!><<!UNRESOLVED_REFERENCE!>X<!>>::y
}

val a = <!NO_THIS, UNRESOLVED_REFERENCE!>this<!><<!UNRESOLVED_REFERENCE!>X<!>>::y

/* GENERATED_FIR_TAGS: functionDeclaration, propertyDeclaration, thisExpression */
