// RUN_PIPELINE_TILL: FRONTEND

interface Inv
class Impl : Inv

class Scope<InterfaceT, ImplementationT : InterfaceT>(private val implClass: <!UNRESOLVED_REFERENCE!>j<!>.<!DEBUG_INFO_MISSING_UNRESOLVED!>Class<!><ImplementationT>) {
    fun foo(c: Collection<InterfaceT>) {
        val hm = c.asSequence()
            .filter(<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>implClass<!>::<!CALLABLE_REFERENCE_RESOLUTION_AMBIGUITY!>isInstance<!>)
            .<!NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>map<!>(<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>implClass<!>::<!CALLABLE_REFERENCE_RESOLUTION_AMBIGUITY!>cast<!>)
            .<!DEBUG_INFO_MISSING_UNRESOLVED!>toSet<!>()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, localProperty, nullableType,
primaryConstructor, propertyDeclaration, typeConstraint, typeParameter */
