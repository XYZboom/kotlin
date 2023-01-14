// WITH_STDLIB
// TARGET_BACKEND: JVM_IR
// ENABLE_JVM_IR_INLINER

fun isBoolVArray(p: Any?) = p is VArray<Boolean>
fun isByteVArray(p: Any?) = p is VArray<Byte>
fun isShortVArray(p: Any?) = p is VArray<Short>
fun isIntVArray(p: Any?) = p is VArray<Int>
fun isLongVArray(p: Any?) = p is VArray<Long>
fun isFloatVArray(p: Any?) = p is VArray<Float>
fun isDoubleVArray(p: Any?) = p is VArray<Double>
fun isCharVArray(p: Any?) = p is VArray<Char>
fun isIcIntVArray(p: Any?) = p is VArray<IcInt>
fun isIcIcIntArray(p: Any?) = p is VArray<IcIcInt>
fun isStringVArray(p: Any?) = p is VArray<String>
fun isIcStrVArray(p: Any?) = p is VArray<IcStr>
fun isDouble3D(p: Any?) = p is VArray<VArray<VArray<Double>>>
fun isString2D(p: Any?) = p is VArray<VArray<String>>
fun isUByteVArray(p: Any?) = p is VArray<UByte>
fun isUShortVArray(p: Any?) = p is VArray<UShort>
fun isUIntVArray(p: Any?) = p is VArray<UInt>
fun isULongVArray(p: Any?) = p is VArray<ULong>


enum class VArrayType {
    BOOL,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    CHAR,
    IcInt,
    STRING,
    IcIcInt,
    IcStr,
    DOUBLE_3D,
    STR_2D,
    U_BYTE,
    U_SHORT,
    U_INT,
    U_LONG,
}

val typeToСheckingFunc = mapOf(
    VArrayType.BOOL to ::isBoolVArray,
    VArrayType.BYTE to ::isByteVArray,
    VArrayType.SHORT to ::isShortVArray,
    VArrayType.INT to ::isIntVArray,
    VArrayType.LONG to ::isLongVArray,
    VArrayType.FLOAT to ::isFloatVArray,
    VArrayType.DOUBLE to ::isDoubleVArray,
    VArrayType.CHAR to ::isCharVArray,
    VArrayType.IcInt to ::isIcIntVArray,
    VArrayType.IcIcInt to ::isIcIcIntArray,
    VArrayType.IcStr to ::isIcStrVArray,
    VArrayType.DOUBLE_3D to ::isDouble3D,
    VArrayType.STR_2D to ::isString2D,
    VArrayType.U_BYTE to ::isUByteVArray,
    VArrayType.U_SHORT to ::isUShortVArray,
    VArrayType.U_INT to ::isUIntVArray,
    VArrayType.U_LONG to ::isULongVArray
)

fun check(obj: Any?, expectedType: VArrayType): Boolean {
    typeToСheckingFunc.forEach { (type, checkingFunc) ->
        if (type == expectedType && !checkingFunc(obj)) return false
        if (type != expectedType && checkingFunc(obj)) return false
    }
    return true
}


@JvmInline
value class IcInt(val x: Int)

@JvmInline
value class IcIcInt(val x: IcInt)

@JvmInline
value class IcStr(val x: String)


fun box(): String {

    val boolVArray = VArray<Boolean>(1) { true }
    val byteVArray = VArray<Byte>(1) { 0.toByte() }
    val shortVArray = VArray<Short>(1) { 0.toShort() }
    val intVArray = VArray<Int>(1) { 0 }
    val longVArray = VArray<Long>(1) { 0.toLong() }
    val floatVArray = VArray<Float>(1) { 0.0.toFloat() }
    val doubleVArray = VArray<Double>(1) { 0.0 }
    val charVArray = VArray<Char>(1) { 'a' }


    val strVArray = VArray<String>(1) { "a" }
    val icIntVArray = VArray<IcInt>(1) { IcInt(42) }
    val icIcIntVArray = VArray<IcIcInt>(1) { IcIcInt(IcInt(42)) }
    val icStrVArray = VArray<IcStr>(1) { IcStr("a") }
    val double3dVArray = VArray(1) { VArray(1) { VArray(1) { 0.0 } } }
    val str2dVArray = VArray(1) { VArray(1) { "a" } }

    val uByteVArray = VArray<UByte>(1) { 0.toUByte() }
    val uShortVArray = VArray<UShort>(1) { 0.toUShort() }
    val uIntVArray = VArray<UInt>(1) { 0.toUInt() }
    val uLongVArray = VArray<ULong>(1) { 0.toULong() }


    if (!check(boolVArray, VArrayType.BOOL)) return "Fail 1"
    if (!check(byteVArray, VArrayType.BYTE)) return "Fail 2"
    if (!check(shortVArray, VArrayType.SHORT)) return "Fail 3"
    if (!check(intVArray, VArrayType.INT)) return "Fail 4"
    if (!check(longVArray, VArrayType.LONG)) return "Fail 5"
    if (!check(floatVArray, VArrayType.FLOAT)) return "Fail 6"
    if (!check(doubleVArray, VArrayType.DOUBLE)) return "Fail 7"
    if (!check(charVArray, VArrayType.CHAR)) return "Fail 8"

    if (!check(icIntVArray, VArrayType.IcInt)) return "Fail 9"
    if (!check(strVArray, VArrayType.STRING)) return "Fail 10"
    if (!check(icIcIntVArray, VArrayType.IcIcInt)) return "Fail 11"
    if (!check(icStrVArray, VArrayType.IcStr)) return "Fail 12"
    if (!check(double3dVArray, VArrayType.DOUBLE_3D)) return "Fail 13"
    if (!check(str2dVArray, VArrayType.STR_2D)) return "Fail 14"

    if (!check(uByteVArray, VArrayType.U_BYTE)) return "Fail 15"
    if (!check(uShortVArray, VArrayType.U_SHORT)) return "Fail 16"
    if (!check(uIntVArray, VArrayType.U_INT)) return "Fail 17"
    if (!check(uLongVArray, VArrayType.U_LONG)) return "Fail 18"

    return "OK"
}

