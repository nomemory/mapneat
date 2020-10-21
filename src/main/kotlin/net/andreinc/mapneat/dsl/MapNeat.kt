package net.andreinc.mapneat.dsl

import net.andreinc.mapneat.model.MapNeatObjectMap
import net.andreinc.mapneat.model.MapNeatSource
import net.andreinc.mapneat.operation.*

class MapNeat(inputJson: String) : MapNeatObjectMap(inputJson) {

    constructor(source: MapNeatSource) : this(source.getStringContent())

    fun assign(init: Assign.() -> Unit) {
        Assign(sourceCtx, targetMap).apply(init).doOperation()
    }

    infix operator fun String.divAssign(constantValue: Any) {
        Assign(sourceCtx, targetMap).apply {
            field = this@divAssign
            value = constantValue
        }.doOperation()
    }

    infix operator fun String.divAssign(acc: AssignOperationMethod) {
        Assign(sourceCtx, targetMap).apply {
            field = this@divAssign
            method = acc
        }.doOperation()
    }

    infix fun String.assign(constantValue: Any) {
        Assign(sourceCtx, targetMap).apply {
            field = this@assign
            value = constantValue
        }.doOperation()
    }

    infix fun String.assign(acc: AssignOperationMethod) {
        Assign(sourceCtx, targetMap).apply {
            field = this@assign
            method = acc
        }.doOperation()
    }

    // Shift transformation DSL methods
    fun shift(init: Shift.() -> Unit) {
        Shift(sourceCtx, targetMap).apply(init).doOperation()
    }

    infix operator fun String.timesAssign(value: String) {
        Shift(sourceCtx, targetMap).apply {
            jsonPath = JsonPathQuery().apply {
                expression = value
            }
            field = this@timesAssign
        }.doOperation()
    }

    infix operator fun String.timesAssign(value: JsonPathQuery.() -> Unit) {
        Shift(sourceCtx, targetMap).apply {
            jsonPath = JsonPathQuery().apply(value)
            field = this@timesAssign
        }.doOperation()
    }

    infix fun String.shift(value: String) {
        Shift(sourceCtx, targetMap).apply {
            jsonPath = JsonPathQuery().apply {
                expression = value
            }
            field = this@shift
        }.doOperation()
    }

    infix fun String.shift(value: JsonPathQuery.() -> Unit) {
        Shift(sourceCtx, targetMap).apply {
            jsonPath = JsonPathQuery().apply(value)
            field = this@shift
        }.doOperation()
    }

    // Delete transformation DSL methods
    fun delete(init: Delete.() -> Unit) {
        Delete(sourceCtx, targetMap).apply(init).doOperation()
    }

    operator fun String.unaryMinus() {
        Delete(sourceCtx, targetMap)
            .apply {
                field = this@unaryMinus
            }
            .doOperation()
    }

    // Move transformation DSL methods
    fun move(init: Move.() -> Unit) {
        Move(sourceCtx, targetMap).apply(init).doOperation()
    }

    infix operator fun String.remAssign(value : String) {
        Move(sourceCtx, targetMap)
            .apply {
                this.field = this@remAssign
                this.newField = value
            }
            .doOperation()
    }

    infix fun String.move(value: String) {
        Move(sourceCtx, targetMap)
            .apply {
                this.field = this@move
                this.newField = value
            }
            .doOperation()
    }

    // DSL Copy
    // Delete transformation DSL methods
    infix fun String.copy(value: String) {
        Copy(sourceCtx, targetMap).apply{
            field = this@copy
            destination = value
        }.doOperation()
    }

    fun copy(init: Copy.() -> Unit) {
        Copy(sourceCtx, targetMap).apply(init).doOperation()
    }

   infix operator fun String.rem(value: String) {
       Copy(sourceCtx, targetMap).apply {
           field = this@rem
           destination = value
       }.doOperation()
   }

    override fun toString(): String {
        return getPrettyString()
    }

    fun json(init: MapNeat.() -> Unit) : Map<String, Any> {
        return MapNeat(super.source)
            .apply(init)
            .getObjectMap()
    }

    fun json(json: String, init: MapNeat.() -> Unit) : Map<String, Any> {
        return MapNeat(json)
                .apply(init)
                .getObjectMap()
    }

    fun json(source: MapNeatSource, init: MapNeat.() -> Unit): Map<String, Any> {
        return MapNeat(source.content)
                .apply(init)
                .getObjectMap()
    }

}

fun json(json: String, init: MapNeat.() -> Unit)  : MapNeat {
    return MapNeat(json)
            .apply(init)
}

fun json(source: MapNeatSource, init: MapNeat.() -> Unit) : MapNeat {
    return MapNeat(source.content)
            .apply(init)
}
