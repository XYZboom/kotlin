// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// KT-11075 NONE_APPLICABLE reported for callable reference to an overloaded generic function with expected type provided

object TestCallableReferences {
    fun <A> foo(x: A) = x
    fun <B> foo(x: List<B>) = x

    fun test0(): (String) -> String = TestCallableReferences::foo

    fun <T> test1(): (List<T>) -> List<T> = TestCallableReferences::foo
}

/* GENERATED_FIR_TAGS: callableReference, functionDeclaration, functionalType, nullableType, objectDeclaration,
typeParameter */
