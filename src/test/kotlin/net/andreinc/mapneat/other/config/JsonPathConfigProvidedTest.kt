package net.andreinc.mapneat.other.config

import com.jayway.jsonpath.Option
import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import net.andreinc.mapneat.config.JsonPathConfiguration
import org.junit.jupiter.api.Test

class JsonPathConfigProvidedTest {
    @Test
    fun `Test provided configuration supresses exceptions`() {
        val config = JsonPathConfiguration.mapNeatConfiguration.addOptions(Option.SUPPRESS_EXCEPTIONS)

        testFromDirectory("config", config) {
            "existing" *= "$.something"
            "notExisting" *= "$.something1"
            "notExistingToRemove" *= "$.something1"
            "notExisting1" /= { sourceCtx().read("$.something1") }
            "notExisting1" % "notExisting2"
            "notExisting2" %= "notExisting3.subPath"
            - "something1"
            - "notExistingToRemove"
        }.doTest()
    }
}