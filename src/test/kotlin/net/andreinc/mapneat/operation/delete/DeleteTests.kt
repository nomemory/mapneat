package net.andreinc.mapneat.operation.delete

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

    @Test
    fun `Deleting fields are working properly (delete method)`() {
        testFromDirectory("delete/simple") {
            "books" *= "$.books"
            "address" *= "$.address"
            delete("address")
        }.doTest()
    }
}