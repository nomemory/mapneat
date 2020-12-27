package net.andreinc.mapneat.other.root

import net.andreinc.mapneat.MapNeatTest
import org.junit.jupiter.api.Test

class RootTests {
    @Test
    fun `Test if the source is copied correctly to target`() {
        MapNeatTest.testFromDirectory("root") {
            copySourceToTarget()
        }.doTest()
    }
}