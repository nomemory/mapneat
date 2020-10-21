**MapNeat** is a JVM library written in Kotlin that provides an easy to use DSL (*Domain Specific Language*) for transforming JSON to JSON, XML to JSON, POJO to JSON in a declarative way. No intermediary POJOs are needed.

Under the hood **MapNeat** is using:
* [jackson](https://github.com/FasterXML/jackson) and [json-path](https://github.com/json-path/JsonPath) for JSON querying and processing;
* [JSON In Java](https://github.com/stleary/JSON-java) for converting from XML to JSON;
* [JSONAssert](http://jsonassert.skyscreamer.org/) for making JSON assertions (testing purposes). 

# Getting Started

The library is still in development. 
For the moment the only way to use it is to clone it, create the jar and included it in your project. 

# How it works

Every transformation on a source JSON, XML or POJO follows the same pattern:

```kotlin
val jsonValue : String = "..."
val transformedJson = json(fromJson(jsonValue)) {
    /* operation1 */
    /* operation2 */
    /* operation3 */
    /* conditional block */
        /* operation4 */
}.getPrettyString() // Transformed output
```

If the source is XML, `fromXML(xmlValue: String)`can be used. In this case the `xmlValue` is automatically converted to JSON using [JSON In Java](https://github.com/stleary/JSON-java).

If the source is a POJO `fromObject(object)` can be used.

# A typical transformation

A typical transformation looks like this:

```kotlin
package net.andreinc.mapneat.examples

import net.andreinc.mapneat.dsl.json
import net.andreinc.mapneat.model.MapNeatSource
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
        // Assigning the citizenship array to a temporary path (person._tmp.array)
        "person._tmp" /= json(fromJson(JSON2)) {
            "array" *= "$.citizenship"
        }

        // We copy the content of temporary array into the path were we want to keep it
        "person._tmp.array" % "person.citizenships"

        // We remove the temporary path
        - "person._tmp"

        // We rename "citizenships" to "citizenship"
        "person.citizenships" %= "person.citizenship"
    }

    println(transform)
}
```

After all the operations are performed step by step, the output looks like this:

```json
{
  "person" : {
    "id" : 100,
    "firstName" : "Gary",
    "lastName" : "Young",
    "meta" : {
      "information1" : "ABC",
      "information2" : "ABC2"
    },
    "maritalStatus" : "unmarried",
    "visited" : [ "Romania", "Italy", "France", "Ireland", "Israel", "Japan" ],
    "citizenship" : [ "Romanian", "French" ]
  }
}
```

# Operations

In the previous example you might wonder what the operators `/=`, `*=`, `%`, `%=`, `-`  are doing. 

Those are actually shortcuts methods for the operations we are performing:

| Operator  | Operation | Description |
| :-------- | :-------- | :-------- |
| `/=` | `assign`   | Assigns a given constant or a value computed in lambda expression to a certain path in the target JSON (the result). |
| `*=` | `shift`    | Shifts a portion from the source JSON based on a JSON Path expression. |
| `%`  | `copy`     | Copies a path from the target JSON to another another path. |
| `%=` | `move`     | Moves a path from the target JSON to another path. |
| `-`  | `delete`   | Deletes a path from the target JSON. |

Additionally, the paths from the target JSON can be "decorated" with "array information"

| Array Information | Description |
| :------- | :------- |
| `path[]` | A `new` array will be created through the `assign` and `shift` operations. |
| `path[+]` | An `append` will be performed through the `assign` and `shift` operations. |
| `path[++]` | A `merge` will be performed through the `assign` and `shift` operations. |
  
If you prefer, instead of using the operators you can use their equivalent methods.

For example:

```
"person.name" /= "Andrei"
```

Can be written as:

```
"person.name" assign "Andrei"
```

Or 

```
"person.name" *= "$.user.full_name"
```   

Can be written as:

```
"person.name" shift "$.user.full_name"
```

What I recommend is to pick a convention and either use operators or method names, but don't mix them.

## Assign (`/=`)

The **Assign** Operation is used to assign a certain value (constant or by evaluating a lambda) to a certain path in the resulting JSON.

Example:

```kotlin
package net.andreinc.mapneat.examples

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
```

And the Output is:
```json
{
  "user" : {
    "user_name" : "neo2020",
    "first_name" : "Gary"
  },
  "more_info" : {
    "married" : false
  },
  "more_info2" : {
    "last_name" : "Young"
  }
}
```

In the lambda method we pass to the `/=` operation we have access to:
* `sourceCtx()` which represents the `ReadContext` of the source. We can use this to read JSON Paths just like in the example above;
* `targetCtx()` which represents the `ReacContext` of the target. This is calculated each time on the method is called, so it contains only the changes that were made up until that point. In most cases this shouldn't be called.

For more information on how to the `ReadContext` in your advantage please read the [json-path](https://github.com/json-path/JsonPath) documentation.

The **Shift** operation can also be used in together with left-side "Array Information" notations (`[]`, `[+]`, `[++]`):

```kotlin
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
```

Output:
```
Simple array creation:
{
  "a" : 1,
  "b" : 1
}
Adds a new value in the array
{
  "a" : [ 1, 2 ],
  "b" : [ 1, true ]
}
Merge in an existing array:
{
  "a" : [ 1, 2 ],
  "b" : [ 1, true, "a", "b", "c" ]
}
```

## Shift (`*=`)

The **Shift** operation is very similar to the *Assign* operation, but it provides an easier way to query the source JSON using [json-path](https://github.com/json-path/JsonPath).

Example:

```kotlin
package net.andreinc.mapneat.examples

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
        // We use an additional processor to capitalise the last Name
        "user.name.last" *= {
            expression = "$.last_name"
            processor = { (it as String).toUpperCase() }
        }
        // We add the photo directly into an array
        "user.photos[]" *= "$.photo"
        // We don't allow duplicates
        "user.visits.countries" *= {
            expression = "$.visits[*].country"
            processor = { (it as MutableList<String>).toSet().toMutableList() }
        }
        // We keep only the last visit
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
```

Output:

```json
{
  "user" : {
    "name" : {
      "first" : "Gary",
      "last" : "YOUNG"
    },
    "photos" : [ "http://srcimg.com/100/150" ],
    "visits" : {
      "countries" : [ "Romania", "Italy", "France" ],
      "lastVisit" : "2020-10-10"
    }
  }
}
```

## Copy (`%`)

The **Copy** Operation moves a certain path from the target JSON to another path in the target JSON.

Example:

```kotlin
package net.andreinc.mapneat.examples

import net.andreinc.mapneat.dsl.json

fun main() {
    val transformed = json("{}") {
        "some.long.path" /= mutableListOf("A, B, C")
        "some.long.path" % "copy"

        println(this)
    }
}
``` 

Output:

```json
{
  "some" : {
    "long" : {
      "path" : [ "A, B, C" ]
    }
  },
  "copy" : [ "A, B, C" ]
}
```

## Move (`%=`)

The **Move** operation moves a certain path from the target JSON to a new path in the target JSON.

Example:

```kotlin
package net.andreinc.mapneat.examples

import net.andreinc.mapneat.dsl.json

fun main() {
    json("{}") {
        "array" /= intArrayOf(1,2,3)
        "array" %= "a.b.c.d"
        println(this)
    }
}
```

Output:

```json
{
  "a" : {
    "b" : {
      "c" : {
        "d" : [ 1, 2, 3 ]
      }
    }
  }
}
```

## Delete (`-`)

The **Delete** operation deletes a certain path from the target JSON.

Example:

```kotlin
package net.andreinc.mapneat.examples

import net.andreinc.mapneat.dsl.json

fun main() {
    json("{}") {
        "a.b.c" /= mutableListOf(1,2,3,4,true)
        "a.b.d" /= "a"
        // deletes the array from "a.b.c"
        - "a.b.c"
        println(this)
    }
}
```

Output:

```json
{
  "a" : {
    "b" : {
      "d" : "a"
    }
  }
}
```
