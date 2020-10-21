import net.andreinc.mapneat.dsl.json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val JSON_VAL = """
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
"""

fun main() {
    val transformed = json(JSON_VAL) {
        "user.name.first" *= "$.first_name"
        "user.name.last" *= {
            expression = "$.last_name"
            processor = { (it as String).toUpperCase() }
        }
        "user.photos[]" *= "$.photo"
        "user.visits.countries" *= {
            expression = "$.visits[*].country"
            processor = { (it as MutableList<String>).toSet().toMutableList() }
        }
        "user.visits.lastVisit" *= {
            expression = "$.visits[*].date"
            processor = {
                (it as MutableList<String>)
                    .stream()
                    .map { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) }
                    .max(LocalDate::compareTo)
                    .get()
                    .toString()
            }
        }
    }

    println(transformed)
}