package net.andreinc.mapneat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import net.andreinc.mapneat.dsl.MapNeat
import net.andreinc.mapneat.model.MapNeatSource.Companion.fromJson
import net.andreinc.mapneat.model.MapNeatSource.Companion.fromXml
import org.skyscreamer.jsonassert.JSONAssert

open class MapNeatTest(private val source: String, private val expected: String, private val init: MapNeat.() -> Unit) {

    private val writer : ObjectWriter = ObjectMapper().writerWithDefaultPrettyPrinter()

    companion object {

        const val SOURCE_XML : String = "/source.xml"
        const val SOURCE_JSON : String = "/source.json"
        const val EXPECTED_JSON : String = "/target.json"

        fun testFromDirectory(dirName: String, xmlSource : Boolean = false, dslInit: MapNeat.() -> Unit) : MapNeatTest {
            val sourceContent = readFile(dirName + if (xmlSource) SOURCE_XML else SOURCE_JSON)
            val source = (if (xmlSource) fromXml(sourceContent) else fromJson(sourceContent)).content
            val expected = readFile(dirName + EXPECTED_JSON)
            return MapNeatTest(source, expected, dslInit)
        }

        private fun readFile(fileName: String) : String {
            return javaClass.classLoader?.getResource(fileName)!!.readText()
        }
    }

    private fun compareExpectedWithActual() {
        val actualObject = MapNeat(source).apply(init).getObjectMap()
        val actualJson = writer.writeValueAsString(actualObject)
        JSONAssert.assertEquals(actualJson, expected, true)
    }

    fun doTest() {
        compareExpectedWithActual()
    }
}
