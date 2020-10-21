import net.andreinc.mapneat.dsl.json
import net.andreinc.mapneat.model.MapNeatSource.Companion.fromJson

val JSON1 = """
{
  "id": 380557,
  "first_name": "Gary",
  "last_name": "Young",
  "photo": "http://srcimg.com/100/150",
  "married": false,
  "visits" : [ 
    {
        "country" : "Romania",
        "date" : "2020-10-10"
    },
    {
        "country" : "Romania",
        "date" : "2019-07-21"
    },
    {
        "country" : "Italy",
        "date" : "2019-12-21"
    },
    {
        "country" : "France",
        "date" : "2019-02-21"
    }
  ]
}
""".trimIndent()

val JSON2 = """
    {
        "citizenship" : [ "Romanian", "French" ]
    }
""".trimIndent()

fun main() {
    val transform = json(fromJson(JSON1)) {

        "person.id"         /= 100
        "person.firstName"  *= "$.first_name"
        "person.lastName"   *= "$.last_name"

        // We can using a nested assignment instead of using the "." notationa
        "person.meta" /= json {
            "information1" /= "ABC"
            "information2" /= "ABC2"
        }

        "person.maritalStatus" /= {
            if(sourceCtx().read("$.married")) "married" else "unmarried"
        }

        "person.visited" *= {
            // We select only the country name
            expression = "$.visits[*].country"
            processor = { countries ->
                // We don't allow duplications so we create a Set
                (countries as List<String>).toMutableSet()
            }
        }

        // We add a new country using the "[+]" notation
        "person.visited[+]" /= "Ireland"

        // We merge another array into the visited[] array
        "person.visited[++]" /= mutableListOf("Israel", "Japan")

        // We look into a secondary json source - JSON2
        // Assigning the citizenship array to a temporary path (person._tmp)
        "person._tmp" /= json(fromJson(JSON2)) {
            "array" *= "$.citizenship"
        }

        // We copy the temporary array into the path were we want to keep it
        "person._tmp.array" % "person.citizenships"

        // We remove the temporary path

        - "person._tmp"
        // We rename "citizenships" to "citizenship"
        "person.citizenships" %= "person.citizenship"
    }

    println(transform)
}