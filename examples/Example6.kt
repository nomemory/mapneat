import net.andreinc.mapneat.dsl.json

fun main() {
    json("{}") {
        "array" /= intArrayOf(1,2,3)
        "array" %= "a.b.c.d"
        println(this)
    }
}