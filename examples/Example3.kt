import net.andreinc.mapneat.dsl.json

fun main() {
    val transformed = json("{}") {
        println("Simple array creation:")
        "a" /= 1
        "b" /= 1
        println(this)

        println("Adds a new value in the array:")
        "a[+]" /= 2
        "b[+]" /= true
        println(this)

        println("Merge in an existing array:")
        "b[++]" /= arrayOf("a", "b", "c")
        println(this)
    }
}