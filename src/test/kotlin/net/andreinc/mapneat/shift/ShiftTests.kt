package net.andreinc.mapneat.shift

import com.jayway.jsonpath.Criteria.where
import com.jayway.jsonpath.Filter.filter
import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test
import java.util.function.Predicate

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

}