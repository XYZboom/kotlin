//!DIAGNOSTICS: -UNUSED_PARAMETER

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> test1(t1: T, t2: @kotlin.internal.NoInfer T): T = t1

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> @kotlin.internal.NoInfer T.test2(t1: T): T = t1

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> test3(t1: @kotlin.internal.NoInfer T): T = t1

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> test4(t1: T, t2: List<@kotlin.internal.NoInfer T>): T = t1

fun usage() {
    test1(1, <!ARGUMENT_TYPE_MISMATCH("kotlin.Int; kotlin.String")!>"312"<!>)
    1.test2("")
    <!NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>test3<!>("")
    test4(1, <!ARGUMENT_TYPE_MISMATCH("kotlin.collections.List<kotlin.Int>; kotlin.collections.List<kotlin.String>")!>listOf("a")<!>)
}

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> List<T>.contains1(e: @kotlin.internal.NoInfer T): Boolean = true

fun test(i: Int?, a: Any, l: List<Int>) {
    l.contains1(<!ARGUMENT_TYPE_MISMATCH!>a<!>)
    l.contains1(<!ARGUMENT_TYPE_MISMATCH!>""<!>)
    l.contains1(<!ARGUMENT_TYPE_MISMATCH!>i<!>)
}

@Suppress("INVISIBLE_MEMBER", <!ERROR_SUPPRESSION!>"INVISIBLE_REFERENCE"<!>)
fun <T> assertEquals1(e1: T, e2: @kotlin.internal.NoInfer T): Boolean = true

fun test(s: String) {
    assertEquals1(s, <!ARGUMENT_TYPE_MISMATCH!>11<!>)
}
