package net.andreinc.mapneat.operation.copy

import net.andreinc.mapneat.MapNeatTest
import org.junit.jupiter.api.Test

class CopyTests {
    @Test
    fun `Copying hierarchies is working properly`() {
        MapNeatTest.testFromDirectory("copy/simple") {
            "store" *= "$.store"
            "store.book" % "store.books"
        }.doTest()
    }

    @Test
    fun `Copying hierarchies is working properly (copy method)`() {
        MapNeatTest.testFromDirectory("copy/simple") {
            "store" *= "$.store"
            "store.book" copy "store.books"
        }.doTest()
    }
}