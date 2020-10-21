package net.andreinc.mapneat.operation.abstract

import net.andreinc.mapneat.exceptions.CannotMergeNonIterableElement

enum class ArrayChange (val isAffecting: Boolean) {
    NEW(true),
    APPEND(true),
    MERGE(true),
    NONE(false)
}

interface MappingOperation {

    fun getMappedValue(): Any

    fun doMappingOperation(current: MutableMap<String, Any>, fieldContext: FieldContext) {
        MappingAction(current, fieldContext, getMappedValue())
            .doAction()
    }
}

@Suppress("UNCHECKED_CAST")
class MappingAction (private val current: MutableMap<String, Any>, private val fieldContext: FieldContext, private val mappedValue: Any) {

    private var field: String = fieldContext.name
    private var arrayChange: ArrayChange = fieldContext.arrayChange

    private fun createField() : MappingAction {
        if (!current.containsKey(field)) {
            if (arrayChange.isAffecting) {
                current[field] = mutableListOf<Any>()
            }
        }
        return this
    }

    private fun iterableToMutableList() : MappingAction {

        if (current[field] is Iterable<*> && current[field] !is MutableList<*>) {
            val result = mutableListOf<Any>()
            result.addAll(current[field] as Iterable<Any>)
            current[field] = result
        }

        when(current[field]) {
            is Array<*> -> current[field] = (current[field] as Array<Any>).toMutableList()
            is ByteArray -> current[field] = (current[field] as ByteArray).toMutableList()
            is CharArray -> current[field] = (current[field] as CharArray).toMutableList()
            is ShortArray -> current[field] = (current[field] as ShortArray).toMutableList()
            is IntArray -> current[field] = (current[field] as IntArray).toMutableList()
            is LongArray -> current[field] = (current[field] as LongArray).toMutableList()
            is DoubleArray -> current[field] = (current[field] as DoubleArray).toMutableList()
            is FloatArray -> current[field] = (current[field] as FloatArray).toMutableList()
            is BooleanArray -> current[field] = (current[field] as BooleanArray).toMutableList()
        }

        return this
    }

    private fun currentAsMutableList() : MutableList<Any> {
        if (!current.containsKey(field)) {
            current[field] = mutableListOf<Any>()
        }
        else {
            if (current[field] !is Iterable<*> &&
                current[field] !is Array<*> &&
                current[field] !is ByteArray &&
                current[field] !is ShortArray &&
                current[field] !is IntArray &&
                current[field] !is IntArray &&
                current[field] !is LongArray &&
                current[field] !is DoubleArray &&
                current[field] !is FloatArray &&
                current[field] !is BooleanArray) {
                current[field] = mutableListOf(current[field] as Any)
            }
        }
        return current[field] as MutableList<Any>
    }

    private fun mapValue() : MappingAction {
        when(arrayChange) {
            ArrayChange.NONE -> current[field] = mappedValue
            ArrayChange.NEW -> current[field] = mutableListOf(mappedValue)
            ArrayChange.APPEND -> currentAsMutableList().add(mappedValue)
            ArrayChange.MERGE -> {
                when(mappedValue) {
                    is Array<*> -> currentAsMutableList().addAll(mappedValue as Array<Any>)
                    is ByteArray -> currentAsMutableList().addAll((mappedValue).toList())
                    is CharArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is ShortArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is IntArray -> currentAsMutableList().addAll((mappedValue).toList())
                    is LongArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is DoubleArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is FloatArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is BooleanArray -> currentAsMutableList().addAll(mappedValue.toList())
                    is Iterable<*> -> currentAsMutableList().addAll(mappedValue as Iterable<Any>)
                    else -> throw CannotMergeNonIterableElement(field)
                }
            }
        }
        return this
    }

    fun doAction() {
        this.createField()
            .iterableToMutableList()
            .mapValue()
    }
}