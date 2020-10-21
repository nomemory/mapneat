package net.andreinc.mapneat.operation.abstract

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.FieldAlreadyExistsAndNotAnObject
import net.andreinc.mapneat.exceptions.OperationFieldIsNotInitialized
import java.lang.ClassCastException

class FieldContext(val path: List<String>, val name: String, val arrayChange: ArrayChange)
typealias FieldAction = (MutableMap<String, Any>, FieldContext) -> Unit

abstract class Operation(private val sourceCtx: ReadContext, val targetMapRef: MutableMap<String, Any>) {

    // The "left-side" field that is affected by the operation
    lateinit var field : String

    // The method that is actually performing the operation
    abstract fun doOperation()

    // The Source Context (The JSON where for example we apply JSON Paths)
    fun sourceCtx(): ReadContext {
        return this.sourceCtx
    }

    // The Target Context
    fun targetCtx(): ReadContext {
        return JsonPath.parse(targetMapRef)
    }

    fun onField(affectedField: String, action: FieldAction) {
        val fieldContext = getFieldContext(affectedField)
        val fullPath = fieldContext.path
        val fieldName = fieldContext.name
        var current = targetMapRef

        for (e in fullPath) {
            try {
                if (!current.containsKey(e)) {
                    current[e] = LinkedHashMap<String, Any>()
                }
                current = current[e] as MutableMap<String, Any>
            } catch (ex: ClassCastException) {
                throw FieldAlreadyExistsAndNotAnObject(fieldName, fullPath)
            }
        }

        action(current, fieldContext)
    }

    fun onSelectedField(action: FieldAction) {

        if (!this::field.isInitialized) {
            throw OperationFieldIsNotInitialized()
        }

        onField(field, action)
    }

    //TODO escape "., [, +, ]"
    private fun getFieldContext(fieldRawValue: String): FieldContext {

        val elements = fieldRawValue.split(".")
        val path = elements.dropLast(1)
        val name: String
        val arrayChange: ArrayChange

        if (elements.last().endsWith("[]")) {
            name = elements.last().dropLast(2)
            arrayChange = ArrayChange.NEW
        } else if (elements.last().endsWith("[+]")) {
            name = elements.last().dropLast(3)
            arrayChange = ArrayChange.APPEND
        } else if (elements.last().endsWith("[++]")) {
            name = elements.last().dropLast(4)
            arrayChange = ArrayChange.MERGE
        } else {
            name = elements.last()
            arrayChange = ArrayChange.NONE
        }

        return FieldContext(path, name, arrayChange)
    }
}

interface StructuralOperation