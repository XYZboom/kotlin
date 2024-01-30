package test

import apt.Anno
import generated.Test as TestGenerated

interface I {
    fun foo() {}
}
@Anno
class Test {
    @field:Anno
    val property: String = ""

    @Anno
    fun function() {

    }
}

fun main() {
    println("Generated class: " + TestGenerated::class.java.name)
}