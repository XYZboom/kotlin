// FIR_IDENTICAL
// WITH_STDLIB
// DUMP_LOCAL_DECLARATION_SIGNATURES
// IGNORE_BACKEND: JS_IR
// IGNORE_BACKEND: JS_IR_ES6

// MUTE_SIGNATURE_COMPARISON_K2: ANY
// ^ KT-57428

annotation class Ann

@delegate:Ann
val test1 by lazy { 42 }
