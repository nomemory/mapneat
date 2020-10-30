package net.andreinc.mapneat.other.parent

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import net.andreinc.mapneat.dsl.json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParentTests {
    @Test
    fun `Test if parent reference is working correctly in inner json reference`() {
        testFromDirectory("parent/simple") {
            "something" /= "Something Value"
            "person" /= json {
                "something" /= { parent()!!.targetCtx().read("$.something") }
            }
        }.doTest()
    }

    @Test
    fun `Test if hasParent() returns a correct value` () {
        json("{}") {
            "value" /= json("{}") {
                assertTrue(hasParent())
            }
        }
    }
}