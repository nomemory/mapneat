package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.CopyOperationNotInitialized
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation

class Copy(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>) : Operation(sourceCtx, targetMapRef), StructuralOperation {

    lateinit var destination: String

    override fun doOperation() {

        if (!this::destination.isInitialized) {
            throw CopyOperationNotInitialized(field)
        }

        onSelectedField { current, fieldContext ->
            val toBeCopied = current.getOrDefault(fieldContext.name, "")
            onField(destination) { newCurrent, newFieldContext ->
                newCurrent[newFieldContext.name] = toBeCopied
            }
        }
    }

}