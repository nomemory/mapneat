package net.andreinc.mapneat.operation

import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.CopyOperationNotInitialized
import net.andreinc.mapneat.operation.abstract.Operation
import net.andreinc.mapneat.operation.abstract.StructuralOperation
import org.apache.logging.log4j.kotlin.Logging

class Copy(sourceCtx: ReadContext, targetMapRef: MutableMap<String, Any>, transformationId : String) :
    Operation(sourceCtx, targetMapRef, transformationId),
    StructuralOperation,
    Logging {

    lateinit var destination: String

    override fun doOperation() {

        if (!this::destination.isInitialized) {
            throw CopyOperationNotInitialized(fullFieldPath)
        }

        onSelectedField { current, fieldContext ->
            val toBeCopied = current.getOrDefault(fieldContext.name, "")
            onField(destination) { newCurrent, newFieldContext ->
                newCurrent[newFieldContext.name] = toBeCopied
                logger.info { "(transformationId=$transformationId) \"${destination}\" COPY(%) ${writer.writeValueAsString(toBeCopied)}"}
            }
        }
    }

}