// RUN_PIPELINE_TILL: FRONTEND
object A {
    object B {
        object A
    }
}

object B

val err = B.<!UNRESOLVED_REFERENCE!>A<!>.B
val correct = A.B.A

/* GENERATED_FIR_TAGS: nestedClass, objectDeclaration, propertyDeclaration */
