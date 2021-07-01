package net.andreinc.mapneat.operation.abstract

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import net.andreinc.mapneat.exceptions.FieldAlreadyExistsAndNotAnObject
import net.andreinc.mapneat.exceptions.OperationFieldIsNotInitialized
import java.lang.ClassCastException
import java.util.*
import kotlin.collections.LinkedHashMap

class FieldContext(val path: List<String>, val name: String, val arrayChange: ArrayChange)
typealias FieldAction = (MutableMap<String, Any?>, FieldContext) -> Unit

abstract class Operation(private val sourceCtx: ReadContext, val targetMapRef: MutableMap<String, Any?>, val transformationId : String = UUID.randomUUID().toString()) {

    companion object {
        // Mainly used for logging purposes
        val writer : ObjectWriter = ObjectMapper()
                                        .writer()
                                        .withDefaultPrettyPrinter()
    }

    // The "left-side" field that is affected by the operation
    lateinit var fullFieldPath : String

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

    fun onField(fullFieldPath: String, action: FieldAction) {
        val fieldContext = getFieldContext(fullFieldPath)
        val fullPath = fieldContext.path
        val fieldName = fieldContext.name
        var current = targetMapRef

        for (e in fullPath) {
            try {
                if (!current.containsKey(e)) {
                    current[e] = LinkedHashMap<String, Any?>()
                }
                current = current[e] as MutableMap<String, Any?>
            } catch (ex: ClassCastException) {
                throw FieldAlreadyExistsAndNotAnObject(fieldName, fullPath)
            }
        }

        action(current, fieldContext)
    }

    fun onSelectedField(action: FieldAction) {

        if (!this::fullFieldPath.isInitialized) {
            throw OperationFieldIsNotInitialized()
        }

        onField(fullFieldPath, action)
    }

    //TODO escape "., [, +, ]"
    private fun getFieldContext(fieldRawValue: String): FieldContext {

        val elements = fieldRawValue.split(".")
        val path = elements.dropLast(1)
        val name: String
        val arrayChange: ArrayChange

        when {
            elements.last().endsWith("[]") -> {
                name = elements.last().dropLast(2)
                arrayChange = ArrayChange.NEW
            }
            elements.last().endsWith("[+]") -> {
                name = elements.last().dropLast(3)
                arrayChange = ArrayChange.APPEND
            }
            elements.last().endsWith("[++]") -> {
                name = elements.last().dropLast(4)
                arrayChange = ArrayChange.MERGE
            }
            else -> {
                name = elements.last()
                arrayChange = ArrayChange.NONE
            }
        }

        return FieldContext(path, name, arrayChange)
    }
}

interface StructuralOperation