fun foo() = "OK"

open class A(val foo: Boolean = true) {
    val ok = foo()
}

val q = object : A() {}

fun box() = q.ok