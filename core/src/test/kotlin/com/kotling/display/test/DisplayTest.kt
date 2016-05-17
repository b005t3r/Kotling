package com.kotling.display.test

import com.badlogic.gdx.math.MathUtils
import com.kotling.display.Display
import com.kotling.display.attribute.name
import junit.framework.TestCase

class DisplayTest : TestCase() {
    companion object {
        val FLOAT_PRECISION = MathUtils.FLOAT_ROUNDING_ERROR * 100f
    }

    lateinit var display:Display

    override fun setUp() {
        display = TestDisplay()
    }

    fun testName() {
        assertEquals("", display.name)

        display.name = "test"
        assertEquals("test", display.name)
    }

    fun testXY() {
        assertEquals(0f, display.x)
        assertEquals(0f, display.y)

        display.x = 1.5f

        assertEquals(1.5f, display.x)
        assertEquals(0f, display.y)

        display.y = 2.333f

        assertEquals(1.5f, display.x)
        assertEquals(2.333f, display.y)
    }

    fun testWidthHeight() {
        assertEquals(TestDisplay.WIDTH, display.width)
        assertEquals(TestDisplay.HEIGHT, display.height)

        display.width = 2.5f

        assertEquals(2.5f, display.width)
        assertEquals(TestDisplay.HEIGHT, display.height)

        display.height = 333.333f

        assertEquals(2.5f, display.width)
        assertEquals(333.333f, display.height)
    }

    fun testPivot() {
        assertEquals(0f, display.pivotX)
        assertEquals(0f, display.pivotY)

        display.pivotX = 3.5f

        assertEquals(3.5f, display.pivotX)
        assertEquals(0f, display.pivotY)

        display.pivotY = 44.33f

        assertEquals(3.5f, display.pivotX)
        assertEquals(44.33f, display.pivotY)
    }

    fun testScale() {
        assertEquals(1f, display.scaleX)
        assertEquals(1f, display.scaleY)

        display.scaleX = 0f

        assertEquals(0f, display.scaleX)
        assertEquals(1f, display.scaleY)

        display.scaleY = 0.333f

        assertEquals(0f, display.scaleX)
        assertEquals(0.333f, display.scaleY)
    }

    fun testBounds() {
        assertEquals(0f, display.bounds.x)
        assertEquals(0f, display.bounds.y)
        assertEquals(TestDisplay.WIDTH, display.bounds.width)
        assertEquals(TestDisplay.HEIGHT, display.bounds.height)

        val x = 5f
        val y = 10f
        val w = 50f
        val h = 20f

        display.x = x
        display.y = y
        display.width = w
        display.height = h

        assertEquals(x, display.bounds.x)
        assertEquals(y, display.bounds.y)
        assertEquals(w, display.bounds.width)
        assertEquals(h, display.bounds.height)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY)
        assertEquals(0f, display.pivotAlignmentX)
        assertEquals(0f, display.pivotAlignmentY)

        display.pivotAlignmentX = 0.5f
        display.pivotAlignmentY = 0.5f

        assertEquals(x - w / 2f, display.bounds.x)
        assertEquals(y - h / 2f, display.bounds.y)
        assertEquals(w, display.bounds.width)
        assertEquals(h, display.bounds.height)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY)
        assertEquals(0.5f, display.pivotAlignmentX)
        assertEquals(0.5f, display.pivotAlignmentY)

        display.rotation = MathUtils.PI2

        assertEquals(x - w / 2f, display.bounds.x)
        assertEquals(y - h / 2f, display.bounds.y)
        assertEquals(w, display.bounds.width)
        assertEquals(h, display.bounds.height)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY)

        display.rotation = MathUtils.PI

        assertEquals(x - w / 2f, display.bounds.x, FLOAT_PRECISION)
        assertEquals(y - h / 2f, display.bounds.y, FLOAT_PRECISION)
        assertEquals(w, display.bounds.width, FLOAT_PRECISION)
        assertEquals(h, display.bounds.height, FLOAT_PRECISION)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX, FLOAT_PRECISION)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY, FLOAT_PRECISION)

        display.rotation = MathUtils.PI / 2

        assertEquals(x - h / 2f, display.bounds.x, FLOAT_PRECISION)
        assertEquals(y - w / 2f, display.bounds.y, FLOAT_PRECISION)
        assertEquals(h, display.bounds.width, FLOAT_PRECISION)
        assertEquals(w, display.bounds.height, FLOAT_PRECISION)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX, FLOAT_PRECISION)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY, FLOAT_PRECISION)

        display.rotation -= MathUtils.PI

        assertEquals(x - h / 2f, display.bounds.x, FLOAT_PRECISION)
        assertEquals(y - w / 2f, display.bounds.y, FLOAT_PRECISION)
        assertEquals(h, display.bounds.width, FLOAT_PRECISION)
        assertEquals(w, display.bounds.height, FLOAT_PRECISION)
        assertEquals(w / TestDisplay.WIDTH, display.scaleX, FLOAT_PRECISION)
        assertEquals(h / TestDisplay.HEIGHT, display.scaleY, FLOAT_PRECISION)
    }
}
