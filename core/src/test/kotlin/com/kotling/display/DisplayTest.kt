package com.kotling.display

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.kotling.poolable.use
import com.kotling.rendering.Painter
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

        display.x = 1.5f
        display.y = 2.333f
        display.width = 15f
        display.height = 230.43f

        assertEquals(1.5f, display.bounds.x)
        assertEquals(2.333f, display.bounds.y)
        assertEquals(15f, display.bounds.width)
        assertEquals(230.43f, display.bounds.height)
        assertEquals(15f / TestDisplay.WIDTH, display.scaleX)
        assertEquals(230.43f / TestDisplay.HEIGHT, display.scaleY)

        display.alignPivot()

        assertEquals(1.5f - 15f / 2f, display.bounds.x)
        assertEquals(2.333f - 230.43f / 2f, display.bounds.y)
        assertEquals(15f, display.bounds.width)
        assertEquals(230.43f, display.bounds.height)
        assertEquals(15f / TestDisplay.WIDTH, display.scaleX)
        assertEquals(230.43f / TestDisplay.HEIGHT, display.scaleY)

        display.rotation = MathUtils.PI2

        assertEquals(1.5f - 15f / 2f, display.bounds.x)
        assertEquals(2.333f - 230.43f / 2f, display.bounds.y)
        assertEquals(15f, display.bounds.width)
        assertEquals(230.43f, display.bounds.height)
        assertEquals(15f / TestDisplay.WIDTH, display.scaleX)
        assertEquals(230.43f / TestDisplay.HEIGHT, display.scaleY)

        display.rotation = MathUtils.PI

        assertEquals(1.5f - 15f / 2f, display.bounds.x, FLOAT_PRECISION)
        assertEquals(2.333f - 230.43f / 2f, display.bounds.y, FLOAT_PRECISION)
        assertEquals(15f, display.bounds.width, FLOAT_PRECISION)
        assertEquals(230.43f, display.bounds.height, FLOAT_PRECISION)
        assertEquals(15f / TestDisplay.WIDTH, display.scaleX, FLOAT_PRECISION)
        assertEquals(230.43f / TestDisplay.HEIGHT, display.scaleY, FLOAT_PRECISION)

        display.rotation = MathUtils.PI / 2

        assertEquals(1.5f - 230.43f / 2f, display.bounds.x, FLOAT_PRECISION)
        assertEquals(2.333f - 15f / 2f, display.bounds.y, FLOAT_PRECISION)
        assertEquals(230.43f, display.bounds.width, FLOAT_PRECISION)
        assertEquals(15f, display.bounds.height, FLOAT_PRECISION)
        assertEquals(15f / TestDisplay.WIDTH, display.scaleX, FLOAT_PRECISION)
        assertEquals(230.43f / TestDisplay.HEIGHT, display.scaleY, FLOAT_PRECISION)
    }
}

class TestDisplay(val initialWidth:Float = WIDTH, val initialHeight:Float = HEIGHT) : Display() {
    companion object {
        val WIDTH   = 10f
        val HEIGHT  = 7f
    }

    override fun getBounds(targetSpace:Display?, result:Rectangle?):Rectangle {
        Pool.Matrix3.use {
            getTransformationMatrix(targetSpace, it)

            val p1 = Pool.Vector2.obtain()
            val p2 = Pool.Vector2.obtain()

            p1.set(0f, 0f).mul(it)
            p2.set(initialWidth, initialHeight).mul(it)

            val out     = result ?: Rectangle()
            out.x       = Math.min(p1.x, p2.x)
            out.y       = Math.min(p1.y, p2.y)
            out.width   = Math.max(p1.x, p2.x) - out.x
            out.height  = Math.max(p1.y, p2.y) - out.y

            Pool.Vector2.free(p1)
            Pool.Vector2.free(p2)

            return out;
        }
    }

    override fun render(painter:Painter) {
        // do nothing
    }
}
