import net.andreinc.mapneat.dsl.json

json(bindings["json"] as String) {
    "name" *= "$.name"
    "a" /= bindings["a"] as String
}.getPrettyString()