// JVM_ABI_K1_K2_DIFF: KT-62714, $ -> . for inner classes

enum class A {
    X {
        val x = "OK"

        inner class Inner {
            val y = x
        }

        val z = Inner()

        override val test: String
            get() = z.y
    };

    abstract val test: String
}

fun box() = A.X.test
