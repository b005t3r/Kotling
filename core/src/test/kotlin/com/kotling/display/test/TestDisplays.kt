package com.kotling.display.test

import com.badlogic.gdx.math.Rectangle
import com.kotling.display.Container
import com.kotling.display.Display
import com.kotling.poolable.use
import com.kotling.rendering.Painter

class TestDisplay(val initialWidth:Float = WIDTH, val initialHeight:Float = HEIGHT) : Display() {
    companion object {
        val WIDTH   = 10f
        val HEIGHT  = 5f
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

            return out
        }
    }

    override fun render(painter:Painter) {
        // do nothing
    }

    override fun toString():String {
        return name
    }
}

class TestContainer : Container() {
    override fun toString():String {
        return children.toString()
    }
}