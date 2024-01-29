// JVM_ABI_K1_K2_DIFF: $ -> . for inner classes
fun box(): String {
    return object {
        val a = A("OK")
        inner class A(val ok: String)
    }.a.ok
}
