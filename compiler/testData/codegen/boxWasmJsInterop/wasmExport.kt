// TARGET_BACKEND: WASM

@JsName("Promise")
external class X

fun c() = kotlin.js.Promise<JsAny> { resolve, reject -> Unit }



fun box(): String {
//    return X::class.isInstance(c()).toString()

    val x = c()
    val y = c()
    val z = X::class


    return z.simpleName

}