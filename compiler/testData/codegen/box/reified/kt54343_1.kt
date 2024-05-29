interface A1
interface A2
open class B

private inline fun<reified B0: B> onB(crossinline onB0 : (B0) -> Unit): (B) -> Unit = { b ->
    if (b is B0) {
        onB0(b)
    }
}

private fun<X> seq(fst: (X) -> Unit, snd: (X) -> Unit): (X) -> Unit = { fst(it); snd(it) }

fun box(): String {
    val onA1: (A1) -> Unit = {}
    val onA2: (A2) -> Unit = {}
    val onA12 /* (A1 & A2) -> Unit */ = seq(onA1, onA2)
    val fb = onB(onA2)
    fb(B())
    return "OK"
}