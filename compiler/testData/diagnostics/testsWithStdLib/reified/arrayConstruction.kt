// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL

fun <T> fail1(): Array<T> = <!TYPE_PARAMETER_AS_REIFIED!>Array<!>(1) { null!! }
fun <T> ok1(block: () -> Array<T>): Array<T> = block()
inline fun <reified T> ok2(): Array<T> = Array(1) { null!! }


fun <T> fail2(): Array<T> = ok1 { Array<<!TYPE_PARAMETER_AS_REIFIED!>T<!>>(1) {  null!! } }
fun <T> ok3(block: () -> Array<T>): Array<T> = ok1 { block() }
inline fun <reified T> ok4(): Array<T> = ok1 { Array<T>(1) { null!! } }

fun <T> fail3(block: () -> T): Pair<Array<T>, Array<T>> = Pair(<!TYPE_PARAMETER_AS_REIFIED!>arrayOf<!>(
        block()), <!TYPE_PARAMETER_AS_REIFIED!>arrayOf<!>()
)
inline fun <reified T> ok5(block: () -> T): Pair<Array<T>, Array<T>> = Pair(
        arrayOf(block()), arrayOf()
)

/* GENERATED_FIR_TAGS: checkNotNullCall, functionDeclaration, functionalType, inline, integerLiteral, lambdaLiteral,
nullableType, reified, typeParameter */
