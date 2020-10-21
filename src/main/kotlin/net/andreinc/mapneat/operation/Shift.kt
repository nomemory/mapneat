package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.JsonPathNotInitialized
import net.andreinc.mapneat.operation.abstract.MappingOperation
import net.andreinc.mapneat.operation.abstract.Operation

class Shift(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>) : Operation(sourceCtx, targetMapRef), MappingOperation {

    lateinit var jsonPath : JsonPathQuery

    override fun doOperation() {
        onSelectedField(::doMappingOperation)
    }

    override fun getMappedValue(): Any {
        if (!this::jsonPath.isInitialized)
            throw JsonPathNotInitialized(field)
        val result =
            sourceCtx().read<Any>(jsonPath.expression)
        return this.jsonPath.processor(result)
    }
}

class JsonPathQuery {
    lateinit var expression : String
    var processor : (input: Any) -> Any = { s -> s }
}