// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-58310
// LANGUAGE: +CheckLambdaAgainstTypeVariableContradictionInResolution

class Inv<T>

fun <T> topLevelOverload(value: T, box: Inv<T>) {}
fun <T> topLevelOverload(value: () -> T, box: Inv<T>) {}

fun <T> topLevelOverload1(box: Inv<T>, value: T) {}
fun <T> topLevelOverload1(box: Inv<T>, value: () -> T) {}

fun <T: CharSequence> topLevelOverload2(box: Inv<T>, value: T) {}
fun <T: CharSequence> topLevelOverload2(box: Inv<T>, value: () -> T) {}

fun <T> topLevelOverload3(box: Inv<T>, value: T) {}
fun <T> topLevelOverload3(box: Inv<T>, value: (param: T) -> Unit) {}

fun <T> Inv<T>.extensionOverload(value: T) {}
fun <T> Inv<T>.extensionOverload(value: () -> T) {}

open class FunHolder {
    fun <T> classMemberOverload(value: T, box: Inv<T>) {}
    fun <T> classMemberOverload(value: () -> T, box: Inv<T>) {}

    fun <T> classMemberOverload1(box: Inv<T>, value: T) {}
    fun <T> classMemberOverload1(box: Inv<T>, value: () -> T) {}

    fun <T: CharSequence> classMemberOverload2(box: Inv<T>, value: T) {}
    fun <T: CharSequence> classMemberOverload2(box: Inv<T>, value: () -> T) {}

    fun <T> classMemberOverload3(box: Inv<T>, value: T) {}
    fun <T> classMemberOverload3(box: Inv<T>, value: (param: T) -> Unit) {}
}

class SubFunHolder: FunHolder() {
    fun <T> classMemberOverload(value: (param: T) -> Unit, box: Inv<T>) {}
    fun <T> classMemberOverload1(box: Inv<T>, value: (param: T) -> Unit) {}
    fun <T: CharSequence> classMemberOverload2(box: Inv<T>, value: (param: T) -> Unit) {}
    fun <T> classMemberOverload3(box: Inv<T>, value: () -> T) {}
}

class ExtensionHolder {
    fun <T> mixedOverload(box: Inv<T>, value: T) {}
}

fun <T> ExtensionHolder.extensionOverload(value: T, box: Inv<T>) {}
fun <T> ExtensionHolder.extensionOverload(value: () -> T, box: Inv<T>) {}

fun <T> ExtensionHolder.extensionOverload1(box: Inv<T>, value: T) {}
fun <T> ExtensionHolder.extensionOverload1(box: Inv<T>, value: () -> T) {}

fun <T: CharSequence> ExtensionHolder.extensionOverload2(box: Inv<T>, value: T) {}
fun <T: CharSequence> ExtensionHolder.extensionOverload2(box: Inv<T>, value: () -> T) {}

fun <T> ExtensionHolder.extensionOverload3(box: Inv<T>, value: T) {}
fun <T> ExtensionHolder.extensionOverload3(box: Inv<T>, value: (param: T) -> Unit) {}

fun <T> ExtensionHolder.mixedOverload(box: Inv<T>, value: () -> T) {}

fun testWithString(box: Inv<String>) {
    topLevelOverload({ "hello" }, (box))
    topLevelOverload1(box) { "hello" }
    topLevelOverload2(box) { "hello" }
    topLevelOverload2(box) { <!RETURN_TYPE_MISMATCH!>1<!> }
    topLevelOverload3(box) { param: String -> }

    FunHolder().classMemberOverload({ "hello" }, (box))
    FunHolder().classMemberOverload1(box) { "hello" }
    FunHolder().classMemberOverload2(box) { "hello" }
    FunHolder().classMemberOverload3(box) { param: String -> }

    SubFunHolder().classMemberOverload ({ param: String -> }, (box))
    SubFunHolder().classMemberOverload1(box) { param: String -> }
    SubFunHolder().classMemberOverload2(box) { param: String -> }
    SubFunHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>classMemberOverload3<!>(box) { "hello" }

    ExtensionHolder().extensionOverload({ "hello" }, (box))
    ExtensionHolder().extensionOverload1(box) { "hello" }
    ExtensionHolder().extensionOverload2(box) { "hello" }
    ExtensionHolder().extensionOverload3(box) { param: String -> }
    ExtensionHolder().mixedOverload(box) { "hello" }

    box.extensionOverload { "hello" }
}

fun testWithAny(box: Inv<Any>) {
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload<!>({ "hello" }, (box))
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload1<!>(box) { "hello" }

    FunHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>classMemberOverload<!>({ "hello" }, (box))
    FunHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>classMemberOverload1<!>(box) { "hello" }

    SubFunHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>classMemberOverload3<!>(box) { "hello" }

    ExtensionHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>extensionOverload<!>({ "hello" }, (box))
    ExtensionHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>extensionOverload1<!>(box) { "hello" }

    box.<!OVERLOAD_RESOLUTION_AMBIGUITY!>extensionOverload<!> { "hello" }
}

fun testWithStringSuper(box: Inv<CharSequence>) {
    topLevelOverload({ "hello" }, (box))
    topLevelOverload1(box) { "hello" }
    topLevelOverload2(box) { "hello" }
    topLevelOverload3(box) { param: CharSequence -> }

    FunHolder().classMemberOverload({ "hello" }, (box))
    FunHolder().classMemberOverload1(box) { "hello" }
    FunHolder().classMemberOverload2(box) { "hello" }
    FunHolder().classMemberOverload3(box) { param: CharSequence -> }

    SubFunHolder().classMemberOverload({ param: CharSequence -> }, (box))
    SubFunHolder().classMemberOverload1(box) { param: CharSequence -> }
    SubFunHolder().classMemberOverload2(box) { param: CharSequence -> }
    SubFunHolder().<!OVERLOAD_RESOLUTION_AMBIGUITY!>classMemberOverload3<!>(box) { "hello" }

    ExtensionHolder().extensionOverload({ "hello" }, (box))
    ExtensionHolder().extensionOverload1(box) { "hello" }
    ExtensionHolder().extensionOverload2(box) { "hello" }
    ExtensionHolder().extensionOverload3(box) { param: CharSequence -> }
    ExtensionHolder().mixedOverload(box) { "hello" }

    box.extensionOverload { "hello" }
}

fun testWithCallableString(box: Inv<() -> String>) {
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload<!>({ "hello" }, (box))
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload1<!>(box) { { "hello" } }
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload3<!>(box) { { param: CharSequence -> } }
    box.<!OVERLOAD_RESOLUTION_AMBIGUITY!>extensionOverload<!> { "hello" }
}

fun testWithCallableAny(box: Inv<() -> Any?>) {
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload<!>({ "hello" }, (box))
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload1<!>(box) { { "hello" } }
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload3<!>(box) { { param: CharSequence -> } }
    box.<!OVERLOAD_RESOLUTION_AMBIGUITY!>extensionOverload<!> { "hello" }
}

fun interface SAM<T> {
    fun single(a: Inv<T>, value: T): Unit
}

fun testSam() {
    val k = SAM { a: Inv<String>, value -> topLevelOverload1(a, value) }
    k.single(Inv<String>(), "")
    val k2 = SAM { a: Inv<()->String>, value -> <!OVERLOAD_RESOLUTION_AMBIGUITY!>topLevelOverload1<!>(a, {value}) }
    k2.single(Inv<()->String>()) { "hello" }
}

// Check error when there is only one candidate
fun <T> noOverloads(box: Inv<T>, value: T) {}

fun testError(box: Inv<String>) {
    noOverloads(box) <!ARGUMENT_TYPE_MISMATCH("Function0<String>; String")!>{ "hello" }<!>
}

fun testOk(box1: Inv<Any>, box2: Inv<() -> Any?>) {
    noOverloads(box1) { "hello" }
    noOverloads(box2) { "hello" }
}

fun <T> twoBoxes(box: Inv<T>, box2: Inv<T>, value: T) {}

fun testContradiction(box1: Inv<Any>, box2: Inv<String>) {
    twoBoxes(box1, <!ARGUMENT_TYPE_MISMATCH!>box2<!>) { "" }
}

/* GENERATED_FIR_TAGS: classDeclaration, funInterface, funWithExtensionReceiver, functionDeclaration, functionalType,
integerLiteral, interfaceDeclaration, lambdaLiteral, localProperty, nullableType, propertyDeclaration, stringLiteral,
typeConstraint, typeParameter */
