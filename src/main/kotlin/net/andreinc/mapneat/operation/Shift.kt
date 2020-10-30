package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.JsonPathNotInitialized
import net.andreinc.mapneat.operation.abstract.MappingOperation
import net.andreinc.mapneat.operation.abstract.Operation
import org.apache.logging.log4j.kotlin.Logging

class Shift(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>, transformationId : String) :
    Operation(sourceCtx, targetMapRef, transformationId),
    MappingOperation,
    Logging {

    lateinit var jsonPath : JsonPathQuery

    override fun doOperation() {
        onSelectedField { current, fieldContext ->
            doMappingOperation(current, fieldContext)
            logger.info { "(transformationId=$transformationId) \"${fullFieldPath}\" SHIFT(*=) ${writer.writeValueAsString(current[fieldContext.name])}" }
        }
    }

    override fun getMappedValue(): Any {
        if (!this::jsonPath.isInitialized)
            throw JsonPathNotInitialized(fullFieldPath)
        val result =
            sourceCtx().read<Any>(jsonPath.expression)
        return this.jsonPath.processor(result)
    }
}

class JsonPathQuery {
    lateinit var expression : String
    var processor : (input: Any) -> Any = { s -> s }
}