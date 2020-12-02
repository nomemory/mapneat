package net.andreinc.mapneat.operation.assign

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
    fun `Constant assignments are working correctly (assign method)`() {
        testFromDirectory("assign/simple") {
            "name" assign  "Andrei"
            "age" assign  15
            "isDisabled" assign  true
            "array1" assign mutableListOf(1, 2, 3)
            "array2" assign  arrayOf(1, 2, 3)
            "array3" assign  setOf("a", "b", "c")
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
    fun `Constant assignments and re-assignments are working correctly (assign method)`() {
        testFromDirectory("assign/re_assign") {
            "name" assign  "Agent"
            "name" assign  "Smith"
            "name" assign "Neo"
            "oneTime" assign  1
            "oneTime" assign  2
            "oneTime" assign  "3"
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
    fun `Constant assignments deep hierarchy are working correctly (assign method)`() {
        testFromDirectory("assign/hierarchy") {
            "a.b.c.d.e.f.g.h" assign  "10"
            "a.b.c.d.e.f.g.i" assign  "20"
            "a.b.c.d.e.f.g.j" assign  "20"
            "a.b.c.d.e.f.g.k" assign  "20"
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
    fun `Lambda assignments are working correctly (assign method)`() {
        testFromDirectory("assign/lambda") {
            "some1" assign { sourceCtx().read("$.someValue") }
            "some2" assign { sourceCtx().read("$.some.val1") }
            "some3" assign { sourceCtx().read("$.some.val2") }
            "some4" assign {
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

    @Test
    fun `Assignments are working correctly with inner JSONs and lambdas (assign method)`() {
        testFromDirectory("assign/jsoninjson") {
            "author" assign  json {
                "fullName" assign  { sourceCtx().read("$.books[1].author") }
                "firstName" assign  { targetCtx().read<String>("$.fullName").split(" ")[0] }
                "lastName" assign  { targetCtx().read<String>("$.fullName").split(" ")[1] }
                - "fullName"
                "book" assign  { sourceCtx().read("$.books[1].title") }
                "address" assign json {
                    "country" /= "UK"
                }
            }
        }.doTest()
    }
}