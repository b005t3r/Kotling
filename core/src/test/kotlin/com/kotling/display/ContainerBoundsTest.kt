package com.kotling.display

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import junit.framework.TestCase

class ContainerBoundsTest : TestCase() {
    lateinit var containerA:Container
    lateinit var containerB:Container
    lateinit var containerC:Container
    lateinit var displayA:Display
    lateinit var displayB:Display
    lateinit var displayC:Display
    lateinit var displayD:Display

    override fun setUp() {
        containerA  = TestContainer()
        containerB  = TestContainer()
        containerC  = TestContainer()
        displayA    = TestDisplay()
        displayB    = TestDisplay()
        displayC    = TestDisplay()
        displayD    = TestDisplay()
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

        displayA.x += 30f
        displayA.y += 120f

        assertEquals(0f, containerA.x)
        assertEquals(0f, containerA.y)
        assertEquals(displayA.x + displayA.width, containerA.width)
        assertEquals(displayA.y + displayA.height, containerA.height)
        assertEquals(Rectangle(0f, 0f, displayA.x + displayA.width, displayA.y + displayA.height), containerA.bounds)
        assertEquals(Rectangle(0f, 0f, displayA.x + displayA.width, displayA.y + displayA.height), containerA.internalBounds)

        displayA.x -= 60f
        displayA.y -= 240f

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
    }
}
