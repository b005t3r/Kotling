package com.kotling.rendering.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Vector2
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import com.kotling.rendering.Vertices
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class VerticesTest {
    lateinit var buffer:Vertices

    @VertexFormat("position:float2", "color:byte4", "uv:float2")
    val testAttributes:VertexAttributes by VertexAttributesCache

    @Before fun setUp() {
        buffer = Vertices(testAttributes)
    }

    @Test fun testAdd() {
        assertEquals(0, buffer.size)

        buffer.add()
        assertEquals(1, buffer.size)

        buffer.add(5)
        assertEquals(6, buffer.size)
    }

    @Test fun testSet() {
        assertEquals(0, buffer.size)

        val x = 1f
        val y = 1f
        val w = 10f
        val h = 10f

        buffer.add(4).
        set(0, 0, x, y).set(0, 2, Color.WHITE).set(0, 3, Vector2(0f, 0f)).
        set(1, 0, x + w, y).set(1, 2, Color.RED).set(1, 3, Vector2(1f, 0f)).
        set(2, 0, x, y + h).set(2, 2, Color.GREEN).set(2, 3, Vector2(0f, 1f)).
        set(3, 0, x + w, y + h).set(3, 2, Color.BLUE).set(3, 3, Vector2(1f, 1f));

        assertEquals(4, buffer.size)
    }
}
