import net.andreinc.mapneat.dsl.json

fun main() {
    val transformed = json("{}") {
        "some.long.path" /= mutableListOf("A, B, C")
        "some.long.path" % "copy"

        println(this)
    }
}