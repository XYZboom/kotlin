// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
class My(val field: Int) {
    // Backing field, initializer
    val second: Int = 0
        get() = field

    // No backing field, no initializer
    val third: Int
        get() = this.field
}

/* GENERATED_FIR_TAGS: classDeclaration, getter, integerLiteral, primaryConstructor, propertyDeclaration, thisExpression */
