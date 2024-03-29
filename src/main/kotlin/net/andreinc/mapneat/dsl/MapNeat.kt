package net.andreinc.mapneat.dsl

import com.jayway.jsonpath.Configuration
import net.andreinc.mapneat.config.JsonPathConfiguration
import net.andreinc.mapneat.model.MapNeatObjectMap
import net.andreinc.mapneat.model.MapNeatSource
import net.andreinc.mapneat.operation.*
import org.apache.logging.log4j.kotlin.Logging
import java.util.*
import kotlin.math.exp

/**
 * This is the starting point of the DSL.
 *
 * The MapNeat class extends MapNeatObjectMap which holds internal representation of a JSON as a MutableMap<String, Any>
 */
class MapNeat(val inputJson: String, val parentObject: MapNeat? = null, val transformationId : String = UUID.randomUUID().toString(), private val jsonPathConfig: Configuration = JsonPathConfiguration.mapNeatConfiguration) : MapNeatObjectMap(inputJson, jsonPathConfig), Logging {

    fun hasParent() : Boolean {
        return parentObject != null
    }

    /**
     * This represents a reference of a possible parent JSON.
     */
    fun parent() : MapNeat? {
        return this.parentObject
    }

    init {
        // All the operations associated with a transformation will be logged using the same ID
        // for an easier tracking inside the logs
        val printInput = if (inputJson == parent()?.inputJson) "INHERITED" else inputJson
        logger.info { "Transformation(id=$transformationId, parentId=${parentObject?.transformationId}) INPUT = $printInput"}
    }

    private constructor(source: MapNeatSource) : this(source.getStringContent())

    infix operator fun String.divAssign(constantValue: Any) {
        Assign(sourceCtx, targetMap, transformationId).apply {
            fullFieldPath = this@divAssign
            value = constantValue
        }.doOperation()
    }

    infix operator fun String.divAssign(acc: AssignOperationMethod) {
        Assign(sourceCtx, targetMap, transformationId).apply {
            fullFieldPath = this@divAssign
            method = acc
        }.doOperation()
    }

    infix fun String.assign(constantValue: Any) {
        Assign(sourceCtx, targetMap, transformationId).apply {
            fullFieldPath = this@assign
            value = constantValue
        }.doOperation()
    }

    infix fun String.assign(acc: AssignOperationMethod) {
        Assign(sourceCtx, targetMap, transformationId).apply {
            fullFieldPath = this@assign
            method = acc
        }.doOperation()
    }

    // Shift transformation DSL methods

    infix operator fun String.timesAssign(value: String) {
        Shift(sourceCtx, targetMap, transformationId).apply {
            jsonPath = JsonPathQuery().apply {
                expression = value
            }
            fullFieldPath = this@timesAssign
        }.doOperation()
    }

    infix operator fun String.timesAssign(value: JsonPathQuery.() -> Unit) {
        Shift(sourceCtx, targetMap, transformationId).apply {
            jsonPath = JsonPathQuery().apply(value)
            fullFieldPath = this@timesAssign
        }.doOperation()
    }

    infix fun String.shift(value: String) {
        Shift(sourceCtx, targetMap, transformationId).apply {
            jsonPath = JsonPathQuery().apply {
                expression = value
            }
            fullFieldPath = this@shift
        }.doOperation()
    }

    infix fun String.shift(value: JsonPathQuery.() -> Unit) {
        Shift(sourceCtx, targetMap, transformationId).apply {
            jsonPath = JsonPathQuery().apply(value)
            fullFieldPath = this@shift
        }.doOperation()
    }

    // Delete transformation DSL methods

    fun delete(value: String) {
        Delete(sourceCtx, targetMap, transformationId)
            .apply {
                fullFieldPath = value
            }.doOperation()
    }

    operator fun String.unaryMinus() {
        Delete(sourceCtx, targetMap, transformationId)
            .apply {
                fullFieldPath = this@unaryMinus
            }
            .doOperation()
    }

    // Move transformation DSL methods

    infix operator fun String.remAssign(value : String) {
        Move(sourceCtx, targetMap, transformationId)
            .apply {
                this.fullFieldPath = this@remAssign
                this.newField = value
            }
            .doOperation()
    }

    infix fun String.move(value: String) {
        Move(sourceCtx, targetMap, transformationId)
            .apply {
                this.fullFieldPath = this@move
                this.newField = value
            }
            .doOperation()
    }

    // Copy transformation DSL methods

    infix fun String.copy(value: String) {
        Copy(sourceCtx, targetMap, transformationId).apply{
            fullFieldPath = this@copy
            destination = value
        }.doOperation()
    }

   infix operator fun String.rem(value: String) {
       Copy(sourceCtx, targetMap, transformationId).apply {
           fullFieldPath = this@rem
           destination = value
       }.doOperation()
   }

    fun copySourceToTarget() {
        Shift(sourceCtx, targetMap, transformationId).apply {
            jsonPath = JsonPathQuery().apply {
                expression = "$"
            }
            fullFieldPath = ""
        }.doOperation()
    }

    override fun toString(): String {
        return getPrettyString()
    }

    fun json(init: MapNeat.() -> Unit) : Map<String, Any?> {
        return MapNeat(super.source, this, transformationId, jsonPathConfig)
            .apply(init)
            .getObjectMap()
    }

    fun json(json: String, init: MapNeat.() -> Unit) : Map<String, Any?> {
        return MapNeat(json, this, transformationId, jsonPathConfig)
            .apply(init)
            .getObjectMap()
    }

    fun json(source: MapNeatSource, init: MapNeat.() -> Unit): Map<String, Any?> {
        return MapNeat(source.content, this, transformationId, jsonPathConfig)
            .apply(init)
            .getObjectMap()
    }

}

inline fun json(json: String, init: MapNeat.() -> Unit)  : MapNeat {
    return MapNeat(json)
            .apply(init)
}

inline fun json(json: String, jsonPathConfig: Configuration, init: MapNeat.() -> Unit)  : MapNeat {
    return MapNeat(json, jsonPathConfig = jsonPathConfig)
        .apply(init)
}

inline fun json(source: MapNeatSource, init: MapNeat.() -> Unit) : MapNeat {
    return MapNeat(source.content)
            .apply(init)
}

inline fun json(source: MapNeatSource, jsonPathConfig: Configuration, init: MapNeat.() -> Unit) : MapNeat {
    return MapNeat(source.content, jsonPathConfig = jsonPathConfig)
        .apply(init)
}