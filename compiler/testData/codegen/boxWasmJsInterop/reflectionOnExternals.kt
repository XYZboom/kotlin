// TARGET_BACKEND: WASM

import kotlin.js.Promise
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@JsName("Promise")
external class PromiseAlias

external class UnknownClass

fun checkPromise(kClass: KClass<*>): String? {
    if (kClass.simpleName != "Promise") return "FAIL1"
    if (kClass != Promise::class) return "FAIL2"
    if (Promise::class != kClass) return "FAIL3"
    return null
}

fun box(): String {
    checkPromise(Promise::class)?.let { return it + "_1" }

    checkPromise(PromiseAlias::class)?.let { return it + "_2" }

    val promiseObj = Promise<JsAny> { resolve, reject -> Unit }
    checkPromise(promiseObj::class)?.let { return it + "_3" }

    if (!Promise::class.isInstance(promiseObj)) return "FAIL_4"

    if (!PromiseAlias::class.isInstance(promiseObj)) return "FAIL_5"

    if (!UnknownClass::class.toString().startsWith("ErrorKClass")) return "FAIL_6"

    val typeOfPromiseClassifier = typeOf<Promise<*>>().classifier
    if (typeOfPromiseClassifier !is KClass<*>) return "FAIL7"
    checkPromise(typeOfPromiseClassifier)?.let { return it + "_8" }

    val typeOfPromiseWithArguments = typeOf<Promise<Promise<*>>>()
    if (typeOfPromiseWithArguments.arguments.size != 1) return "FAIL9"
    val typeOfPromiseArgumentClassifier = typeOfPromiseWithArguments.arguments[0].type?.classifier
    if (typeOfPromiseArgumentClassifier !is KClass<*>) return "FAIL10"
    checkPromise(typeOfPromiseArgumentClassifier)?.let { return it + "_11" }

    return "OK"
}