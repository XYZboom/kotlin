// MUTE_SIGNATURE_COMPARISON_K2: JS_IR
// MUTE_SIGNATURE_COMPARISON_K2: NATIVE
// ^ KT-57818

import Host.objectMember

object Host {
    inline fun <reified T> objectMember(x: T) {}
}

inline fun <reified T> topLevel1(x: T) {}
inline fun <reified T> topLevel2(x: List<T>) {}

val test1: (Int) -> Unit = ::topLevel1

val test2: (List<String>) -> Unit = ::topLevel2

val test3: (Int) -> Unit = ::objectMember
