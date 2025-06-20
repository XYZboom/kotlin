// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-64840
class Controller<T> {
    fun yield(t: T): Boolean = true
}

fun <S> generate(g: suspend Controller<S>.() -> Unit): S = TODO()

interface A<F> {
    val a: F
}

interface B<G> : A<G> {
    val b: G
}

fun <X> withCallback(x: X, c: Controller<in X>, p: (X) -> Unit) {}

fun main(a: A<String>) {
    val x = generate {
        withCallback(a, this) {
            (it as B).b.length
            <!DEBUG_INFO_SMARTCAST!>it<!>.b.length
            it.a.length
        }
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("A<kotlin.String>")!>x<!>
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, functionDeclaration, functionalType, inProjection,
interfaceDeclaration, lambdaLiteral, localProperty, nullableType, propertyDeclaration, smartcast, suspend,
thisExpression, typeParameter, typeWithExtension */
