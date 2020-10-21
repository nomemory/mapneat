package net.andreinc.mapneat.delete

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test

class DeleteTests {
    @Test
    fun `Deleting fields are working properly`() {
        testFromDirectory("delete/simple") {
            "books" *= "$.books"
            "address" *= "$.address"
            - "address"
        }.doTest()
    }
}