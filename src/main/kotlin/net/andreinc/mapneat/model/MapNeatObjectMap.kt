package net.andreinc.mapneat.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.config.JsonPathConfiguration.mapNeatConfiguration

open class MapNeatObjectMap (val source: String, private val jsonPathConfig : Configuration = mapNeatConfiguration) {

    protected val targetMap = LinkedHashMap<String, Any?>()
    protected val sourceCtx: ReadContext = JsonPath.using(jsonPathConfig).parse(source)

    fun sourceCtx() : ReadContext {
        return this.sourceCtx
    }

    fun targetCtx() : ReadContext {     
        return JsonPath.using(mapNeatConfiguration).parse(targetMap)
    }

    fun getObjectMap() : Map<String, Any?> {
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
}
