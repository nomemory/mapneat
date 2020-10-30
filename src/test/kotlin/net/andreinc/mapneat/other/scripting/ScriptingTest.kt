package net.andreinc.mapneat.other.scripting

import net.andreinc.mapneat.scripting.KotlinScriptRunner
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

const val jsonValue = """
    {
        "name" : "x"
    }
"""

const val result = """
    {
        "name" : "x",
        "a" : "123"
    }
"""

class ScriptingTest {
    @Test
    fun `Check if script is correctly executed`() {
        val transformed = KotlinScriptRunner.evalFile(
            "scripting/Transformation.kts", readAsResource = true, compileFirst = true, mapOf(
                "json" to jsonValue,
                "a" to "123"
            )
        ) as String
        JSONAssert.assertEquals(transformed, result, true)
    }
}