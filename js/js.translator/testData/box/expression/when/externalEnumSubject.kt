// CHECK_IF_COUNT: function=foo count=2
external enum class X {
    A, B
}

fun box(): String {
    if (foo(X.A) != "!") return "fail1: ${foo(X.A)}"
    if (foo(X.B) != "@") return "fail2: ${foo(X.B)}"

    return "OK"
}

fun foo(x: X) = when (x) {
    X.A -> "!"
    X.B -> "@"
}