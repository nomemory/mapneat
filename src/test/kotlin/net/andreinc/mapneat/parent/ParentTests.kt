package net.andreinc.mapneat.parent

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
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
}