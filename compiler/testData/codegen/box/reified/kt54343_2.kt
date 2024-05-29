// WITH_STDLIB

interface IA {
    fun func(): String
}

interface IB

data class Foo<T0>(val arg: T0)
class Bar<T1> {
    inline fun <reified S : T1> func(t: Foo<in S>): S? {
        return t.arg as? S
    }
}


fun box(): String {
    val bar = Bar<IA>()
    val foo = Foo<IB>(object : IB {})
    val i = bar.func(foo)
    if (i?.func() == null) {
        return "OK"
    }
    return "Fail"
}

/*
inline fun <reified T> func(t: Any): Int {
    if (t is T) {
        return 1
    }
    return 2
}

fun box(): String {
    func<() -> Unit> {
        println()
    }
    return "Fail"
}*/
