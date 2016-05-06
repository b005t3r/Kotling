package com.kotling.texture.test

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.assertRectEquals
import com.kotling.assertVecEquals
import com.kotling.texture.TexturePatch
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class TexturePatchTest {
    companion object {
        const val TEX_WIDTH:Int     = 200
        const val TEX_HEIGHT:Int    = 100

        const val x = 20f
        const val y = 30f
        const val w = 50f
        const val h = 20f
    }

    lateinit var texture:Texture

    @Before fun setUp() {
        texture = Mockito.mock(Texture::class.java)
        Mockito.`when`(texture.width).thenReturn(TEX_WIDTH)
        Mockito.`when`(texture.height).thenReturn(TEX_HEIGHT)
    }

    @Test fun testNotRotated() {
        var region = Rectangle(x, y, w, h)
        val patch = TexturePatch(texture, region)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testRotatedClockwise() {
        var region = Rectangle(x, y, h, w)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.CLOCKWISE)
        val minUV = Vector2(x / TEX_WIDTH + h / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-h / TEX_WIDTH, w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testRotatedCounterclockwise() {
        var region = Rectangle(x, y, h, w)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.COUNTERCLOCKWISE)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT + w / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(h / TEX_WIDTH, -w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testRotatedUpsideDown() {
        var region = Rectangle(x, y, w, h)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.UPSIDE_DOWN)
        val maxUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val minUV = Vector2().set(maxUV).add(w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testHorizontalFlip() {
        var region = Rectangle(x, y, w, h)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.HORIZONTAL_FLIP)
        val minUV = Vector2(x / TEX_WIDTH + w / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-w / TEX_WIDTH, h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testVerticalFlip() {
        var region = Rectangle(x, y, w, h)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.VERTICAL_FLIP)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT + h / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(w / TEX_WIDTH, -h / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testRotatedClockwiseFlip() {
        var region = Rectangle(x, y, h, w)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.CLOCKWISE_FLIP)
        val minUV = Vector2(x / TEX_WIDTH, y / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(h / TEX_WIDTH, w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }

    @Test fun testRotatedCounterclockwiseFlip() {
        var region = Rectangle(x, y, h, w)
        val patch = TexturePatch(texture, region, transform = TexturePatch.Transform.COUNTERCLOCKWISE_FLIP)
        val minUV = Vector2(x / TEX_WIDTH + h / TEX_WIDTH, y / TEX_HEIGHT + w / TEX_HEIGHT)
        val maxUV = Vector2().set(minUV).add(-h / TEX_WIDTH, -w / TEX_HEIGHT)

        assertRectEquals(region, patch.region)

        assertVecEquals(minUV, patch.minUV)
        assertVecEquals(maxUV, patch.maxUV)
    }
}
