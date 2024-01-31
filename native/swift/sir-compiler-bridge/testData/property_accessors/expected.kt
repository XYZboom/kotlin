import kotlin.native.internal.ExportedBridge

@ExportedBridge("getter_bridge")
public fun getter_bridge(): Boolean {
    val result = var()
    return result
}

@ExportedBridge("setter_bridge")
public fun setter_bridge(newValue: Boolean): Unit {
    val result = var(newValue)
    return result
}
