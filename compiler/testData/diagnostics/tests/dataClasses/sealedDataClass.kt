// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +DataClassCopyRespectsConstructorVisibility
<!INCOMPATIBLE_MODIFIERS!>sealed<!> <!INCOMPATIBLE_MODIFIERS!>data<!> class My(val x: Int) {
    object Your: My(1)
    class His(y: Int): My(y)
}

/* GENERATED_FIR_TAGS: classDeclaration, data, integerLiteral, nestedClass, objectDeclaration, primaryConstructor,
propertyDeclaration, sealed */
