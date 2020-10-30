package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.MoveOperationNotInitialized
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation
import org.apache.logging.log4j.kotlin.Logging

/**
 * A transformation that moves a branch from one place to another inside the target output
 * It can be also used to rename fields
 */
class Move(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>, transformationId : String) :
    Operation(sourceCtx, targetMapRef, transformationId),
    StructuralOperation,
    Logging {

    lateinit var newField : String

    override fun doOperation() {

        if (!this::newField.isInitialized) {
            throw MoveOperationNotInitialized(fullFieldPath)
        }

        onSelectedField { current, fieldContext  ->
            if (current.containsKey(fieldContext.name)) {

                val toBeMoved = current.getOrDefault(fieldContext.name, "")

                Assign(sourceCtx(), targetMapRef, transformationId).apply {
                    this.fullFieldPath = newField
                    this.value = toBeMoved
                }.doOperation()

                Delete(sourceCtx(), targetMapRef, transformationId).apply {
                    this.fullFieldPath = super.fullFieldPath
                }.doOperation()

            }
        }
    }
}