import net.andreinc.mapneat.dsl.json

fun main() {
    json("{}") {
        "a.b.c" /= mutableListOf(1,2,3,4,true)
        "a.b.d" /= "a"
        - "a.b.c"
        println(this)
    }
}