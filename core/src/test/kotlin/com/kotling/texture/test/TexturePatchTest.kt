package com.kotling.texture.test

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.assertRectEquals
import com.kotling.assertVecEquals
import com.kotling.texture.TexturePatch
import com.kotling.texture.transform
import com.kotling.util.mul
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class TexturePatchTest {
    companion object {
        const val TEX_WIDTH:Int     = 200
        const val TEX_HEIGHT:Int    = 100

        const val x = 20f
        const val y = 30f
        const val w = 70f
        const val h = 30f

        // frame's offsets, relative to region
        const val left = 10f
        const val top = 10f
        const val right = 20f
        const val bottom = 20f
    }

    lateinit var texture:Texture

    @Before fun setUp() {
        texture = Mockito.mock(Texture::class.java)
        Mockito.`when`(texture.width).thenReturn(TEX_WIDTH)
        Mockito.`when`(texture.height).thenReturn(TEX_HEIGHT)
    }

    @Test fun testNotRotated() {
        var region = Rectangle(x, y, w, h)
        var transformedFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testRotatedClockwise() {
        var region = Rectangle(x, y, h, w)
        var transformedFrame = Rectangle(-bottom, -left, h + top + bottom, w + left + right)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.CLOCKWISE)
        val minUV = Vector2(x / TEX_WIDTH + h / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-h / TEX_WIDTH, w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testRotatedCounterclockwise() {
        var region = Rectangle(x, y, h, w)
        var transformedFrame = Rectangle(-top, -right, h + top + bottom, w + left + right)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.COUNTERCLOCKWISE)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT + w / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(h / TEX_WIDTH, -w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testRotatedUpsideDown() {
        var region = Rectangle(x, y, w, h)
        var transformedFrame = Rectangle(-right, -bottom, w + left + right, h + top + bottom)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.UPSIDE_DOWN)
        val maxUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val minUV = Vector2().set(maxUV).add(w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testHorizontalFlip() {
        var region = Rectangle(x, y, w, h)
        var transformedFrame = Rectangle(-right, -top, region.width + left + right, region.height + top + bottom)
        var originalFrame = Rectangle(-left, -top, region.width + left + right, region.height + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.HORIZONTAL_FLIP)
        val minUV = Vector2(x / TEX_WIDTH + w / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testVerticalFlip() {
        var region = Rectangle(x, y, w, h)
        var transformedFrame = Rectangle(-left, -bottom, region.width + left + right, region.height + top + bottom)
        var originalFrame = Rectangle(-left, -top, region.width + left + right, region.height + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.VERTICAL_FLIP)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT + h / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(w / TEX_WIDTH, -h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testRotatedClockwiseFlip() {
        var region = Rectangle(x, y, h, w)
        var transformedFrame = Rectangle(-top, -left, h + top + bottom, w + left + right)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.CLOCKWISE_FLIP)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(h / TEX_WIDTH, w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }

    @Test fun testRotatedCounterclockwiseFlip() {
        var region = Rectangle(x, y, h, w)
        var transformedFrame = Rectangle(-bottom, -right, h + top + bottom, w + left + right)
        var originalFrame = Rectangle(-left, -top, w + left + right, h + top + bottom)
        val patch = TexturePatch(texture, region, transformedFrame, transform = TexturePatch.Transform.COUNTERCLOCKWISE_FLIP)
        val minUV = Vector2(x / TEX_WIDTH + h / TEX_WIDTH, y / TEX_HEIGHT + w / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-h / TEX_WIDTH, -w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)

        assertRectEquals(transformedFrame, patch.frame)
        assertRectEquals(originalFrame, Rectangle(transformedFrame).transform(patch))
    }
}
