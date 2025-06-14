// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// OPT_IN: kotlin.RequiresOptIn
// DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE

@file:OptIn(ExperimentalTypeInference::class)

import kotlin.experimental.ExperimentalTypeInference

class GenericController<T> {
    suspend fun yield(t: T) {}
}

fun <S> generate(g: suspend GenericController<S>.() -> Unit): List<S> = TODO()

val test1 = generate {
    yield(generate {
        yield(generate {
            yield(generate {
                yield(3)
            })
        })
    })
}

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, classDeclaration, classReference, functionDeclaration,
functionalType, integerLiteral, lambdaLiteral, nullableType, propertyDeclaration, suspend, typeParameter,
typeWithExtension */
