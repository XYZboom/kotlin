// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL

private const val A = 0L
private val B = 0L
private fun sample() = 0L

private class PrivateClass

class Foo {
    var bar: Long = 0
    private var other: PrivateClass? = null

    init {
        bar = A
        bar = B
        bar = sample()
        other = PrivateClass()
    }

    constructor()
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, const, functionDeclaration, init, nullableType, propertyDeclaration,
secondaryConstructor */
