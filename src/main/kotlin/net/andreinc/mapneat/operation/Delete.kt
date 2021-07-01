package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation
import org.apache.logging.log4j.kotlin.Logging

/**
 * A transformation that deletes a certain field and all of it's children
 */
class Delete(sourceCtx: ReadContext, mapReference: MutableMap<String, Any?>, transformationId : String) :
    Operation(sourceCtx, mapReference, transformationId),
    StructuralOperation,
    Logging {
    override fun doOperation() {
        onSelectedField { current, fieldContext ->
            current.remove(fieldContext.name)
            logger.info { "(transformationId=$transformationId) DELETE(-) \"${fullFieldPath}\""}
        }
    }
}