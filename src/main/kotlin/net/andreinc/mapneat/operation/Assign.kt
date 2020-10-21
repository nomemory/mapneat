package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.AssignOperationNotInitialized
import net.andreinc.mapneat.model.MapNeatObjectMap
import net.andreinc.mapneat.operation.abstract.MappingOperation
import net.andreinc.mapneat.operation.abstract.Operation

typealias AssignOperationMethod = () -> Any

class Assign(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>) : Operation(sourceCtx, targetMapRef),
    MappingOperation {

    lateinit var value : Any
    lateinit var method: AssignOperationMethod

    override fun doOperation() {
        onSelectedField(::doMappingOperation)
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
            throw AssignOperationNotInitialized(field)
        }
    }

}