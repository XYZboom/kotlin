// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
class Outer {
    <!INCOMPATIBLE_MODIFIERS!>inner<!> <!INCOMPATIBLE_MODIFIERS!>data<!> class Inner(val x: Int)
}
