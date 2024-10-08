package foo

class A() {
    public var param: Int = 0

    inline public fun setParam(value: Int) {
        val b = B(value)
        b.setParam(this)
    }
}

class B(val value: Int) {
    inline fun setParam(a: A) {
        a.param = this.value
    }
}

// CHECK_BREAKS_COUNT: function=box count=0
// CHECK_LABELS_COUNT: function=box name=$l$block count=0
public fun box(): String {
    val a = A()
    a.setParam(10)
    assertEquals(10, a.param)

    return "OK"
}