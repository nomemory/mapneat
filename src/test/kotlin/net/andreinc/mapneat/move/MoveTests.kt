package net.andreinc.mapneat.move

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
}