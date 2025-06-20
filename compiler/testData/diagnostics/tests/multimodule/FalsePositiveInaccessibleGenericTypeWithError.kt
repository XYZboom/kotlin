// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ForbidUsingExpressionTypesWithInaccessibleContent
// ISSUE: KT-66690
// MODULE: base
// FILE: base.kt

class Generic<T>

// MODULE: intermediate(base)
// FILE: intermediate.kt

class Owner<T>

interface Some<S> {
    val g: Owner<Generic<S>>
}

fun register(owner: Owner<*>) {}

// MODULE: user(intermediate)
// FILE: user.kt

fun test(some: Some<String>) {
    register(some.g)
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, nullableType, propertyDeclaration,
starProjection, typeParameter */
