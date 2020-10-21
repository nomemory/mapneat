package net.andreinc.mapneat.exceptions

import java.lang.RuntimeException

abstract class MapNeatException(message: String) : RuntimeException(message)

class FieldAlreadyExistsAndNotAnObject(fieldName: String, fullPath: List<String>) :
        MapNeatException("Cannot create path '${fullPath.joinToString("/")}/${fieldName}'. Field already exists and it's not an object.")

class JsonPathNotInitialized(target: String) :
        MapNeatException("Cannot execute operation, the jsonPath object is not initialised for the '${target}'")

class AssignOperationNotInitialized(fieldName: String) :
        MapNeatException("Assign Operation is not initialized properly for field: '${fieldName}'. Either 'method' or 'value' needs to be initialized first.")

class MoveOperationNotInitialized(fieldName: String) :
        MapNeatException("Move Operation is not initialized properly for field: '${fieldName}'. The target 'newField' needs to be initialised first.")

class CopyOperationNotInitialized(fieldName: String) :
        MapNeatException("Copy Operation is not initialized properly for field: '${fieldName}. The target 'destination' needs to be initialised first.")

class OperationFieldIsNotInitialized() :
        MapNeatException("Operation is not initialized properly. 'field' needs to be initialized.")

class CannotMergeNonIterableElement(fieldName: String) :
        MapNeatException("Cannot merge non-iterable value in '${fieldName}'.")
