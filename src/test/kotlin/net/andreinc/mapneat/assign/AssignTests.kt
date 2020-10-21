package net.andreinc.mapneat.assign

import net.andreinc.mapneat.MapNeatTest.Companion.testFromDirectory
import org.junit.jupiter.api.Test

class AssignTests {
    @Test
    fun `Constant assignments are working correctly`() {
        testFromDirectory("assign/simple") {
            "name" /= "Andrei"
            "age" /= 15
            "isDisabled" /= true
            "array1" /= mutableListOf(1, 2, 3)
            "array2" /= arrayOf(1, 2, 3)
            "array3" /= setOf("a", "b", "c")
        }.doTest()
    }

    @Test
    fun `Constant assignments and re-assignments are working correctly`() {
        testFromDirectory("assign/re_assign") {
            "name" /= "Agent"
            "name" /= "Smith"
            "name" /= "Neo"
            "oneTime" /= 1
            "oneTime" /= 2
            "oneTime" /= "3"
        }.doTest()
    }

    @Test
    fun `Constant assignments deep hierarchy are working correctly`() {
        testFromDirectory("assign/hierarchy") {
            "a.b.c.d.e.f.g.h" /= "10"
            "a.b.c.d.e.f.g.i" /= "20"
            "a.b.c.d.e.f.g.j" /= "20"
            "a.b.c.d.e.f.g.k" /= "20"
        }.doTest()
    }

    @Test
    fun `Lambda assignments are working correctly`() {
        testFromDirectory("assign/lambda") {
            "some1" /= { sourceCtx().read("$.someValue") }
            "some2" /= { sourceCtx().read("$.some.val1") }
            "some3" /= { sourceCtx().read("$.some.val2") }
            "some4" /= {
                mutableListOf<Any>(
                    targetCtx().read("$.some1"),
                    sourceCtx().read("$.some.val1"),
                    sourceCtx().read("$.some.val2")
                )
            }
        }.doTest()
    }

    @Test
    fun `Assignments are working correctly with inner JSONs and lambdas`() {
        testFromDirectory("assign/jsoninjson") {
            "author" /= json {
                "fullName" /= { sourceCtx().read("$.books[1].author") }
                "firstName" /= { targetCtx().read<String>("$.fullName").split(" ")[0] }
                "lastName" /= { targetCtx().read<String>("$.fullName").split(" ")[1] }
                - "fullName"
                "book" /= { sourceCtx().read("$.books[1].title") }
                "address" /= json {
                    "country" /= "UK"
                }
            }
        }.doTest()
    }
}