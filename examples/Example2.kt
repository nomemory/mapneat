import net.andreinc.mapneat.dsl.json

const val A_SRC_1 = """
{
    "id": 380557,
    "first_name": "Gary",
    "last_name": "Young"
}    
"""

const val A_SRC_2 = """
{
    "photo": "http://srcimg.com/100/150",
    "married": false
}
"""

fun main() {
    val transformed = json(A_SRC_1) {
        // Assigning a constant
        "user.user_name" /= "neo2020"

        // Assigning value from a lambda expression
        "user.first_name" /= { sourceCtx().read("$.first_name") }

        // Assigning value from another JSON source
        "more_info" /= json(A_SRC_2) {
            "married" /= { sourceCtx().read("$.married") }
        }

        // Assigning an inner JSON with the same source as the parent
        "more_info2" /= json {
            "last_name" /= { sourceCtx().read("$.last_name") }
        }
    }
    println(transformed)
}

