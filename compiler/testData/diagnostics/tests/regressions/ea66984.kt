// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -NO_VALUE_FOR_PARAMETER
class Tree<<!REDECLARATION!>T<!>>(T <!SYNTAX!>element<!>, <!SYNTAX!><!>Tree<T><!SYNTAX!><!> left<!SYNTAX!><!>, <!SYNTAX!><!>Tree<T><!SYNTAX!><!> right<!SYNTAX!><!>) {}

/* GENERATED_FIR_TAGS: classDeclaration, nullableType, primaryConstructor, typeParameter */
