package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.MoveOperationNotInitialized
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation

/**
 * A transformation that moves a branch from one place to another inside the target output
 * It can be also used to rename fields
 */
class Move(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>) : Operation(sourceCtx, targetMapRef), StructuralOperation {

    lateinit var newField : String

    override fun doOperation() {

        if (!this::newField.isInitialized) {
            throw MoveOperationNotInitialized(field)
        }

        onSelectedField { current, fieldContext  ->
            if (current.containsKey(fieldContext.name)) {

                val toBeMoved = current.getOrDefault(fieldContext.name, "")

                Assign(sourceCtx(), targetMapRef).apply {
                    this.field = newField
                    this.value = toBeMoved
                }.doOperation()

                Delete(sourceCtx(), targetMapRef).apply {
                    this.field = super.field
                }.doOperation()

            }
        }
    }
}