// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// LANGUAGE: +QualifiedSupertypeMayBeExtendedByOtherSupertype
interface IBase<T> {
    fun foo() {}
    fun bar() {}
}

interface IDerived<T> : IBase<T> {
    override fun foo() {}
    fun qux() {}
}

class Test : IDerived<String>, IBase<String> {
    fun test() {
        super<IBase>.foo()
        super<IBase>.bar()
        super<IDerived>.foo()
        super<IDerived>.bar()
        super<IDerived>.qux()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration, nullableType, override,
superExpression, typeParameter */
