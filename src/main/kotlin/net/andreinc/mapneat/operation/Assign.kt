package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext

import net.andreinc.mapneat.exceptions.AssignOperationNotInitialized
import net.andreinc.mapneat.model.MapNeatObjectMap
import net.andreinc.mapneat.operation.abstract.MappingOperation
import net.andreinc.mapneat.operation.abstract.Operation
import org.apache.logging.log4j.kotlin.Logging

typealias AssignOperationMethod = () -> Any

class Assign(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>, transformationId : String) :
    Operation(sourceCtx, targetMapRef, transformationId),
    MappingOperation,
    Logging {

    lateinit var value : Any
    lateinit var method: AssignOperationMethod

    override fun doOperation() {
        onSelectedField { current, fieldContext ->
            doMappingOperation(current, fieldContext)
            val content = writer.writeValueAsString(current[fieldContext.name])
            logger.info { "(transformationId=${transformationId}) \"${fullFieldPath}\" ASSIGN(/=) ${content}" }
        }
    }

    override fun getMappedValue(): Any {
        return if (this::value.isInitialized) {
            if (value is MapNeatObjectMap) {
                (value as MapNeatObjectMap).getObjectMap()
            } else {
                value
            }
        } else if (this::method.isInitialized) {
            method()
        } else {
            throw AssignOperationNotInitialized(fullFieldPath)
        }
    }

}