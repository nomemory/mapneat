package net.andreinc.mapneat.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.config.JsonPathConfiguration.mapNeatConfiguration
import net.andreinc.mapneat.operation.abstract.Operation

open class MapNeatObjectMap (val source: String) {

    protected val targetMap = LinkedHashMap<String, Any>()
    protected val sourceCtx: ReadContext = JsonPath.using(mapNeatConfiguration).parse(source)
    protected val operations: MutableList<Operation> = mutableListOf()

    fun sourceCtx() : ReadContext {
        return this.sourceCtx
    }

    fun targetCtx() : ReadContext {     
        return JsonPath.using(mapNeatConfiguration).parse(targetMap)
    }

    fun getObjectMap() : Map<String, Any> {
        return targetMap
    }

    fun getPrettyString() : String {
        return ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(targetMap)
    }

    fun getString() : String {
        return ObjectMapper().writeValueAsString(targetMap)
    }

    fun execute() {
        operations.forEach { it.doOperation() }
    }
}
