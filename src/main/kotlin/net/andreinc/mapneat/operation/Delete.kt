package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation

/**
 * A transformation that deletes a certain field and all of it's children
 */
class Delete(sourceCtx: ReadContext, mapReference: MutableMap<String, Any>) : Operation(sourceCtx, mapReference), StructuralOperation {
    override fun doOperation() {
        onSelectedField { current, fieldContext ->
            current.remove(fieldContext.name)
        }
    }
}