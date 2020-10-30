package net.andreinc.mapneat.scripting

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.io.File
import java.lang.IllegalStateException
import java.nio.charset.Charset
import javax.script.*

object KotlinScriptRunner {

    init {
        setIdeaIoUseFallback()
    }

    private val scriptManager = ScriptEngineManager(currentClassLoader())
    private val scriptEngine : ScriptEngine = scriptManager.getEngineByExtension("kts")!!

    fun getScriptEngine() : ScriptEngine {
        return this.scriptEngine
    }

    private fun readResource(resource: String, charset: Charset = Charsets.UTF_8) : String {
        val resourceUrl = javaClass.classLoader?.getResource(resource)
        return resourceUrl!!.readText(charset)
    }

    private fun currentClassLoader() : ClassLoader {
        return Thread.currentThread().contextClassLoader
    }

    fun eval(scriptContent: String, compileFirst: Boolean = true, bindings: Map<String, String> = mapOf()) : Any {
        if (compileFirst) {
            when (scriptEngine) {
                is Compilable -> {
                    val compiledCode = scriptEngine.compile(scriptContent)
                    return compiledCode.eval(scriptEngine.createBindings().apply { putAll(bindings) })
                }
                else -> throw IllegalStateException("Kotlin is not an instance of Compilable. Cannot compile script.")
            }
        }
        else {
            return scriptEngine.eval(scriptContent, scriptEngine.createBindings().apply { putAll(bindings) })
        }
    }

    fun evalFile(file: String, readAsResource: Boolean, compileFirst: Boolean = true, bindings: Map<String, String> = mapOf()) : Any {
        val content : String = if (readAsResource) readResource(file) else File(file).readText()
        return eval(content, compileFirst, bindings)
    }
}
