// RUN_PIPELINE_TILL: FRONTEND
class Outer {
    inner class Inner
}

val x = Outer.<!NO_COMPANION_OBJECT!>Inner<!>
val klass = Outer.Inner::class

/* GENERATED_FIR_TAGS: classDeclaration, classReference, inner, propertyDeclaration */
