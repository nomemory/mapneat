package net.andreinc.mapneat.experimental.scripting

import org.apache.logging.log4j.kotlin.Logging
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

data class ProvidedProperty(val name: String, val type: KClass<*>, val value: Any?) {
    constructor(name: String, type: Class<*>, value: Any?) : this(name, type.kotlin, value)
}

/**
 * Experimental feature, run kotlin transformations from .kts scripts
 */
object KotlinScriptRunner : Logging {

    fun eval(sourceCode: SourceCode, props: List<ProvidedProperty>): ResultWithDiagnostics<EvaluationResult> {

        val compileConfig = ScriptCompilationConfiguration {
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
                defaultImports("net.andreinc.mapneat.dsl.*")
            }
            providedProperties(*(props.map { it.name to KotlinType(it.type) }.toTypedArray()))
        }
        val evaluationConfig = ScriptEvaluationConfiguration {
            providedProperties(*(props.map { it.name to it.value }.toTypedArray()))
        }

        return BasicJvmScriptingHost().eval(sourceCode, compileConfig, evaluationConfig)
    }

    fun evalAsString(sourceCode: SourceCode, props: List<ProvidedProperty>) : String {
        return eval(sourceCode, props).valueOrThrow().returnValue.toString()
    }
}

//fun main() {
//
//    val script = """
//        json("{}") {
//            "a" /= "Andrei"
//            "b" /= aValue
//        }
//    """
//
//    val props1 = listOf(
//        ProvidedProperty("aValue", Int::class, 3)
//    )
//
//    val props2 = listOf(
//        ProvidedProperty("aValue", Int::class, 4)
//    )
//
//    repeat(1) {
//        println(evalAsString(script.toScriptSource(), props2))
//    }
//}