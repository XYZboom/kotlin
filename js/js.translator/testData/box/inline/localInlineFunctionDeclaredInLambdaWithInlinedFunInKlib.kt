// LANGUAGE: +IrInlinerBeforeKlibSerialization
// IGNORE_BACKEND: JS_IR, JS_IR_ES6
// ^^^ KT-75937: error: <missing declarations>: No function found for symbol 'foo/localWithoutCapture|localWithoutCapture(kotlin.Int;kotlin.Int){}[0]:2:3' declared in file localInlineFunctionDeclaredInLambdaWithInlinedFunInKlib.kt
// After fix, please merge this test with `localInlineFunctionDeclaredInLambda.kt`
package foo

// CHECK_CONTAINS_NO_CALLS: localWithCapture except=Unit_getInstance
// CHECK_CONTAINS_NO_CALLS: localWithoutCapture

internal inline fun repeatAction(times: Int, action: () -> Unit) {
    for (i in 1..times) {
        action()
    }
}

// CHECK_BREAKS_COUNT: function=localWithoutCapture count=0
// CHECK_LABELS_COUNT: function=localWithoutCapture name=$l$block count=0
internal fun localWithoutCapture(a: Int, b: Int): Int {
    var mult = 0

    repeatAction(a) {
        inline fun inc(x: Int): Int {
            return x + 1
        }

        repeatAction(b) {
            mult = inc(mult)
        }
    }

    return mult
}

// CHECK_BREAKS_COUNT: function=localWithCapture count=0
// CHECK_LABELS_COUNT: function=localWithCapture name=$l$block count=0
internal fun localWithCapture(a: Int, b: Int): Int {
    var mult = 0

    repeatAction(a) {
        inline fun inc() {
            mult++
        }

        repeatAction(b) {
            inc()
        }
    }

    return mult
}

fun box(): String {
    assertEquals(2, localWithoutCapture(1, 2), "localWithoutCapture")
    assertEquals(20, localWithoutCapture(4, 5), "localWithoutCapture")

    assertEquals(2, localWithCapture(1, 2), "localWithCapture")
    assertEquals(20, localWithCapture(4, 5), "localWithCapture")

    return "OK"
}