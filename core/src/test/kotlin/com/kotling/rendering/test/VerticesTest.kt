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

        assertEquals(Vector2(x, y), buffer.get(0, 0, null as Vector2?))
        assertEquals(Color.WHITE, buffer.get(0, 2, null as Color?))
        assertEquals(Vector2(0f, 0f), buffer.get(0, 3, null as Vector2?))

        assertEquals(Vector2(x + w, y), buffer.get(1, 0, null as Vector2?))
        assertEquals(Color.RED, buffer.get(1, 2, null as Color?))
        assertEquals(Vector2(1f, 0f), buffer.get(1, 3, null as Vector2?))

        assertEquals(Vector2(x, y + h), buffer.get(2, 0, null as Vector2?))
        assertEquals(Color.GREEN, buffer.get(2, 2, null as Color?))
        assertEquals(Vector2(0f, 1f), buffer.get(2, 3, null as Vector2?))

        assertEquals(Vector2(x + w, y + h), buffer.get(3, 0, null as Vector2?))
        assertEquals(Color.BLUE, buffer.get(3, 2, null as Color?))
        assertEquals(Vector2(1f, 1f), buffer.get(3, 3, null as Vector2?))
    }
}
