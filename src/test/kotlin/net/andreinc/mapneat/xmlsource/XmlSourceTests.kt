package net.andreinc.mapneat.xmlsource

import net.andreinc.mapneat.MapNeatTest
import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test

class XmlSourceTests {

    @Test
    fun `Simple test to check the XML source works correctly`() {
        testFromDirectory("xmlsource/simple", xmlSource = true) {
            "feeling" *= "$.root.somehow.howHungry"
            "feeling[+]" *= "$.root.somehow.content"
        }.doTest()
    }
}