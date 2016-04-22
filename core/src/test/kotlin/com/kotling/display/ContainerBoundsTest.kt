package com.kotling.display

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
}