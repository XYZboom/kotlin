// RUN_PIPELINE_TILL: BACKEND
// WITH_STDLIB
// ISSUE: KT-53349

class SomeClass

val SomeClass.lore: List<String>
    get() {
        apply {
            return emptyList()
        }
    }

/* GENERATED_FIR_TAGS: classDeclaration, getter, lambdaLiteral, propertyDeclaration, propertyWithExtensionReceiver */
