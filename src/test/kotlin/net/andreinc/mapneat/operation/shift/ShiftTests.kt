package net.andreinc.mapneat.operation.shift

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test

class ShiftTests {

    @Test
    fun `Simple shifts are working properly` () {
        testFromDirectory("shift/simple") {
            "books" *= "$.store.book[*].title"
            "books[+]" /= "Game of Thrones"
            "genre" *= {
                expression = "$.store.book[*].category"
                processor = { books ->
                    (books as MutableList<String>).toSet().toMutableList()
                }
            }
        }.doTest()
    }

    @Test
    fun `Simple shifts are working properly (shift method)` () {
        testFromDirectory("shift/simple") {
            "books" shift "$.store.book[*].title"
            "books[+]" /=  "Game of Thrones"
            "genre" shift  {
                expression = "$.store.book[*].category"
                processor = { books ->
                    (books as MutableList<String>).toSet().toMutableList()
                }
            }
        }.doTest()
    }

    @Test
    fun `Simple shifts with leniency are working properly (shift)` () {
        testFromDirectory("shift/lenient") {
            "books" *=  {
                expression = "$.store.broken.path"
                lenient = true
            }
            "books[++]" *= {
                expression = "$.store.book[*].title"
            }
            println(this)
        }.doTest()
    }
}