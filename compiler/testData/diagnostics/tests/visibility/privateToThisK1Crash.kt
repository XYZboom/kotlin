// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-55446
val Any?.tt: String
    get() = ""

open class Base<in T>(t: T) {
    private val tt = t.also { foo(null!!) }

    fun foo(a: Derived<String>) = run {
        val x: String = a.tt
    }
}

class Derived<in T>(t: T) : Base<T>(t)

/* GENERATED_FIR_TAGS: checkNotNullCall, classDeclaration, functionDeclaration, getter, in, lambdaLiteral, localProperty,
nullableType, primaryConstructor, propertyDeclaration, propertyWithExtensionReceiver, stringLiteral, typeParameter */
