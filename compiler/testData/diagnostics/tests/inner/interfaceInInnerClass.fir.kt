// RUN_PIPELINE_TILL: FRONTEND
class Outer {
    inner class Inner {
        <!NESTED_CLASS_NOT_ALLOWED!>interface TestNestedInterface<!>
    }
}
