package net.andreinc.mapneat.operation.move

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test

class MoveTests {

    @Test
    fun `Moving hierarchies is working properly`() {
        testFromDirectory("move/simple") {
            "store" *= "$.store"
            "store.book" %= "store.books"
        }.doTest()
    }

    @Test
    fun `Moving hierarchies is working properly (move method)`() {
        testFromDirectory("move/simple") {
            "store" *= "$.store"
            "store.book" move "store.books"
        }.doTest()
    }
}