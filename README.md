**MapNeat** is a JVM library written in Kotlin that provides an easy to use DSL (*Domain Specific Language*) for transforming JSON to JSON, XML to JSON, POJO to JSON in a declarative way. 

No intermediary POJOs are needed. Given's Kotlin high-interoperability **MapNeat** can be used in a Java project without any particular hassle. Check the documentation for examples on how to do that. 

Under the hood **MapNeat** is using:
* [jackson](https://github.com/FasterXML/jackson) and [json-path](https://github.com/json-path/JsonPath) for JSON querying and processing;
* [JSON In Java](https://github.com/stleary/JSON-java) for converting from XML to JSON;
* [JSONAssert](http://jsonassert.skyscreamer.org/) for making JSON assertions (testing purposes).

# Table of contents

* [Getting started](#getting-started)
* [How it works](#how-it-works)
* [A typical transformation](#a-typical-transformation)
* [Operations](#operations)
    * [Assign](#assign-)
    * [Shift](#shift-)
    * [Copy](#copy-)
    * [Move](#move-)
    * [Delete](#delete--)  
* [Using Mapneat from Java](#using-mapneat-from-java)
* [Logging](#logging)
* [Contributions and Roadmap](#contributing-and-roadmap)

# Getting Started

The library is available in [jcenter()](https://bintray.com/nomemory/maven/mapneat) and can use used with Maven and Gradle:

```kotlin
repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    //
   implementation "net.andreinc.mapneat:mapneat:0.9.4"
    // 
}
``` 

# How it works

The library will transform any JSON, XML or POJO into another JSON, without the need of intermediary POJO classes.

Every operation applied to the source JSON (the input) is declarative in nature, and involves significantly less code than writing everything by hand.

Normally, a transformation has the following structure:

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

If the source is a POJO, `fromObject(object)` can be used. In this case the `object` is automatically converted to JSON using jackson.

# A typical transformation

A typical transformation looks like this:

JSON1:
```json
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
```

JSON2:
```json
{
  "citizenship" : [ "Romanian", "French" ]
}
```

We write the **MapNeat** transformation like:

```kotlin
fun main() {
    // JSON1 and JSON2 are both String variables 
    val transform = json(fromJson(JSON1)) {

        "person.id"         /= 100
        "person.firstName"  *= "$.first_name"
        "person.lastName"   *= "$.last_name"

        // We can using a nested json assignment instead of using the "." notation
        "person.meta" /= json {
            "information1" /= "ABC"
            "information2" /= "ABC2"
        }
        
        // We can assign a value from a lambda expression
        "person.maritalStatus" /= {
            if(sourceCtx().read("$.married")) 
                "married" 
            else 
                "unmarried"
        }

        "person.visited" *= {
            // We select only the country name from the visits array
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

        // We rename "citizenships" to "citizenship" because we don't like typos
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

Additionally, the paths from the target JSON can be "decorated" with "array notation":

| Array Notation | Description |
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

Personally, I prefer the operator notation (`/=`, `*=`, etc.), but some people consider the methods (`assign`, `shift`) more readable. 

For the rest of the examples the operator notation will be used.

## Assign (`/=`)

The **Assign** Operation is used to assign a value to a path in the resulting JSON (target).

The value can be a constant object, or a lambda (`()-> Any`).

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

Output:
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
* `targetCtx()` which represents the `ReacContext` of the target. This is calculated each time we call the method. So, it contains only the changes that were made up until that point. In most cases this shouldn't be called.

In case we are using an inner JSON structure, we also have reference to the parent source and target contexts:
* `parent.sourceCtx()`
* `parent.targetCtx()`

`parent()` returns a nullable value, so it needs to be used adding `!!` (double bang).

```
... {
    "something" /= "Something Value"
    "person" /= json {
        "innerSomething" /= { parent()!!.targetCtx().read("$.something") }
    }
}
```

For more information about `ReadContext`s please check [json-path](https://github.com/json-path/JsonPath)'s documentation.

The **Assign** operation can also be used in conjunction with left-side array notations (`[]`, `[+]`, `[++]`):

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

As you can see in the above example, each expression can be accompanied with an additional processor method that allows developers to refine the results provided by the JSON path expression.

Similar to the **Assign** lambdas, `sourceCtx()`, `targetCtx()`, `parent!!.sourceCtx()`, `parent!!.targetCtx()` are also available to the method context and can be used.

If you want to `Shift` all the source JSON into the target you can use the following transformation:

```
"" *= "$
```

Or call the `copySourceToTarget()` method directly.

In case a field is optional, and you don't want automatically fail the mapping, you can use the `leniency` property:

```kotlin
"books" *=  {
                expression = "$.store.broken.path"
                lenient = true
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

# Using **MapNeat** from Java

Given Kotlin's high level of interoperability with Java, **MapNeat** can be used in any Java application.

The DSL file should remain kotlin, but it can be called from any Java program, as simple as:

```kotlin
@file : JvmName("Sample")

package kotlinPrograms

import net.andreinc.mapneat.dsl.json

fun personTransform(input: String) : String {
    return json(input) {
        "person.name" /= "Andrei"
        "person.age" /= 13
    }.getPrettyString()
}
```

The java file:

```java
import static kotlinPrograms.Sample.personTransform;

public class Main {
    public static void main(String[] args) {
        // personTransform(String) is the method from Kotlin
        String person = personTransform("{}");
        System.out.println(person);
    }
}
```

PS: Configuring the Java application to be Kotlin-enabled it's quite simple, usually IntelliJ is doing this automatically without amy developer intervention.

# Logging

The library uses log4j2 for logging purposes. 

Each transformation gets logged by default to `SYSTEM_OUT`and to `logs/mapneat.log`.

For tracing and debugging purposes transformations have two IDs (id, parentId - if inner JSONs are used).

E.g.:

```
19:05:06.204 [main] INFO  net.andreinc.mapneat.dsl.MapNeat - Transformation(id=a739ba94-dedd-4d5b-bd09-03b30693a1ae, parentId=null) INPUT = {
  "books" : [
    {
      "title" : "Cool dog",
      "author" : "Mike Smith"
    },
    {
      "title": "Feeble Cat",
      "author": "John Cibble"
    },
    {
      "title": "Morning Horse",
      "author": "Kohn Gotcha"
    }
  ],
  "address" : {
    "country" : "RO",
    "street_number": 123,
    "city": "Bucharest"
  }
}
19:05:06.209 [main] INFO  net.andreinc.mapneat.dsl.MapNeat - Transformation(id=a739ba94-dedd-4d5b-bd09-03b30693a1ae, parentId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) INPUT = INHERITED
19:05:06.244 [main] INFO  net.andreinc.mapneat.operation.Assign - (transformationId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) "fullName" ASSIGN(/=) "John Cibble"
19:05:06.246 [main] INFO  net.andreinc.mapneat.operation.Assign - (transformationId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) "firstName" ASSIGN(/=) "John"
19:05:06.246 [main] INFO  net.andreinc.mapneat.operation.Assign - (transformationId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) "lastName" ASSIGN(/=) "Cibble"
19:05:06.248 [main] INFO  net.andreinc.mapneat.operation.Delete - (transformationId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) DELETE(-) "fullName"
```

`(transformationId=a739ba94-dedd-4d5b-bd09-03b30693a1ae)` => represents the id
`Transformation(id=a739ba94-dedd-4d5b-bd09-03b30693a1ae, parentId=a739ba94-dedd-4d5b-bd09-03b30693a1ae) INPUT = INHERITED` => marks the parentId
   
# Contributing and Roadmap

The highlevel roadmap for the library at this moment is:
1. Make mapneat a command-line tool
2. Create a mapneat-server to serve transformation sync / async

Anyone if free to contribute. You know how github works:).


----------------

For more code examples, please check: https://github.com/nomemory/mapneat
