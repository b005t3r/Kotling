package com.kotling.display.test

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.kotling.display.Container
import com.kotling.display.Display
import junit.framework.Assert
import junit.framework.TestCase

class ContainerBoundsTest : TestCase() {
    lateinit var containerA:Container
    lateinit var containerB:Container
    lateinit var containerC:Container
    lateinit var displayA:Display
    lateinit var displayB:Display
    lateinit var displayC:Display
    lateinit var displayD:Display
    lateinit var displayE:Display

    override fun setUp() {
        containerA  = TestContainer()
        containerB  = TestContainer()
        containerC  = TestContainer()
        displayA    = TestDisplay("a")
        displayB    = TestDisplay("b")
        displayC    = TestDisplay("c")
        displayD    = TestDisplay("d")
        displayE    = TestDisplay("e")
    }

    fun testEmpty() {
        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(0f, containerA.width)
        assertEquals(0f, containerA.height)
        assertEquals(Rectangle(), containerA.bounds)
        assertEquals(Rectangle(), containerA.internalBounds)
    }

    fun testWithChild() {
        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(0f, containerA.width)
        assertEquals(0f, containerA.height)
        assertEquals(Rectangle(), containerA.bounds)
        assertEquals(Rectangle(), containerA.internalBounds)

        containerA.children.add(displayA)

        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(displayA.width, containerA.width)
        assertEquals(displayA.height, containerA.height)
        assertEquals(Rectangle(0f, 0f, displayA.width, displayA.height), containerA.bounds)
        assertEquals(Rectangle(0f, 0f, displayA.width, displayA.height), containerA.internalBounds)

        displayA.x += 50
        displayA.y += 100f

        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(displayA.x + displayA.width, containerA.width)
        assertEquals(displayA.y + displayA.height, containerA.height)
        assertEquals(Rectangle(0f, 0f, displayA.x + displayA.width, displayA.y + displayA.height), containerA.bounds)
        assertEquals(Rectangle(0f, 0f, displayA.x + displayA.width, displayA.y + displayA.height), containerA.internalBounds)

        displayA.x -= 100f
        displayA.y -= 200f

        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(Math.abs(displayA.x), containerA.width)
        assertEquals(Math.abs(displayA.y), containerA.height)
        assertEquals(Rectangle(displayA.x, displayA.y, Math.abs(displayA.x), Math.abs(displayA.y)), containerA.bounds)
        assertEquals(Rectangle(displayA.x, displayA.y, Math.abs(displayA.x), Math.abs(displayA.y)), containerA.internalBounds)

        containerA.x += 100f
        containerA.y += 200f

        assertEquals(100f, containerA.x)
        assertEquals(200f, containerA.y)
        assertEquals(Math.abs(displayA.x), containerA.width)
        assertEquals(Math.abs(displayA.y), containerA.height)
        assertEquals(Rectangle(100f + displayA.x, 200f + displayA.y, Math.abs(displayA.x), Math.abs(displayA.y)), containerA.bounds)
        assertEquals(Rectangle(displayA.x, displayA.y, Math.abs(displayA.x), Math.abs(displayA.y)), containerA.internalBounds)

        displayA.rotation = MathUtils.PI / 2

        assertEquals(100f, containerA.x)
        assertEquals(200f, containerA.y)
        assertEquals(Math.abs(displayA.x) + displayA.width, containerA.width)
        assertEquals(Math.abs(displayA.y), containerA.height)
        assertEquals(Rectangle(100f + displayA.x - displayA.width, 200f + displayA.y, Math.abs(displayA.x) + displayA.width, Math.abs(displayA.y)), containerA.bounds)
        assertEquals(Rectangle(displayA.x - displayA.width, displayA.y, Math.abs(displayA.x) + displayA.width, Math.abs(displayA.y)), containerA.internalBounds)
    }

    fun testWithChildren() {
        val w = 10f
        val h = 10f

        displayA.width = w
        displayA.height = h
        displayB.width = w
        displayB.height = h
        displayC.width = w
        displayC.height = h

        displayA.x = -20f
        displayA.y = -30f

        displayB.x = 0f
        displayB.y = 10f
        //displayB.pivotAlignmentY = 1f
        displayB.rotation = -MathUtils.PI / 2

        displayC.x = 40f
        displayC.y = -20f
        displayC.pivotAlignmentX = 1f
        displayC.pivotAlignmentY = 1f
        displayC.rotation = -MathUtils.PI / 2

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)

        containerA.children.add(displayA)
        containerA.children.add(displayB)
        containerA.children.add(displayC)

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)

        displayD.x = 60f
        displayD.y = 35f
        displayD.width = w
        displayD.height = h
        displayD.rotation = -MathUtils.PI

        assertRectEquals(Rectangle(50f, 25f, 10f, 10f), displayD.bounds)

        containerB.children.add(containerA)
        containerB.children.add(displayD)

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)
        assertRectEquals(Rectangle(50f, 25f, 10f, 10f), displayD.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 80f, 65f), containerB.bounds)

        displayD.scaleX *= 7f
        displayD.scaleY *= 8f

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)
        assertRectEquals(Rectangle(-10f, -45f, 70f, 80f), displayD.bounds)
        assertRectEquals(Rectangle(-20f, -45f, 80f, 80f), containerB.bounds)

        containerB.scaleY = 0.5f

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)
        assertRectEquals(Rectangle(-10f, -45f, 70f, 80f), displayD.bounds)
        assertRectEquals(Rectangle(-20f, -22.5f, 80f, 40f), containerB.bounds)

        displayE.x = -20f
        displayE.y = 20f
        displayE.width = 10f
        displayE.height = 10f
        displayE.pivotAlignmentX = 1f

        containerC.children.add(displayE)
        containerC.children.add(containerB)

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)
        assertRectEquals(Rectangle(-10f, -45f, 70f, 80f), displayD.bounds)
        assertRectEquals(Rectangle(-20f, -22.5f, 80f, 40f), containerB.bounds)
        assertRectEquals(Rectangle(-30f, 20f, 10f, 10f), displayE.bounds)
        assertRectEquals(Rectangle(-30f, -22.5f, 90f, 52.5f), containerC.bounds)

        containerC.scaleX = 1f / 3f

        assertRectEquals(Rectangle(-20f, -30f, 10f, 10f), displayA.bounds)
        assertRectEquals(Rectangle(0f, 0f, 10f, 10f), displayB.bounds)
        assertRectEquals(Rectangle(30f, -20f, 10f, 10f), displayC.bounds)
        assertRectEquals(Rectangle(-20f, -30f, 60f, 40f), containerA.bounds)
        assertRectEquals(Rectangle(-10f, -45f, 70f, 80f), displayD.bounds)
        assertRectEquals(Rectangle(-20f, -22.5f, 80f, 40f), containerB.bounds)
        assertRectEquals(Rectangle(-30f, 20f, 10f, 10f), displayE.bounds)
        assertRectEquals(Rectangle(-10f, -22.5f, 30f, 52.5f), containerC.bounds)

        assertRectEquals(Rectangle(-6.666666f, -15f, 3.333333f, 5f), displayA.getBounds(null))
        //println(displayA.getBounds(displayE))
        //println(displayE.getBounds(displayA))
    }
}

@Suppress("DEPRECATION")
fun assertRectEquals(rectA:Rectangle, rectB:Rectangle) {
    if(! cmpRect(rectA, rectB))
        Assert.failNotEquals(null, rectA, rectB)
}

fun cmpRect(rectA:Rectangle, rectB:Rectangle):Boolean {
    return MathUtils.isEqual(rectA.x, rectB.x, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.y, rectB.y, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.width, rectB.width, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.height, rectB.height, DisplayTest.FLOAT_PRECISION);
}