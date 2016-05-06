package com.kotling.rendering.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.assertRectEquals
import com.kotling.rendering.Indices
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

    @Test fun testCopyTo() {
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

        val anotherBuffer = Vertices(testAttributes, 0)

        assertEquals(0, anotherBuffer.size)

        buffer.copyTo(anotherBuffer) { vertexID, targetVertexID, attrID, src, srcOffset, dst, dstOffset, count ->
            for (i in 0..count - 1)
                dst[dstOffset + i] = src[srcOffset + i]
        }

        assertEquals(buffer, anotherBuffer)

        val yetAnotherBuffer = Vertices(testAttributes, 20)

        assertEquals(0, yetAnotherBuffer.size)

        buffer.copyTo(yetAnotherBuffer)

        assertEquals(buffer, yetAnotherBuffer)
        assertEquals(anotherBuffer, yetAnotherBuffer)
    }

    @Test fun testForEach() {
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

        val anotherBuffer = Vertices(testAttributes, 0)

        assertEquals(0, anotherBuffer.size)

        buffer.forEach { vertexID, attrID, src, srcOffset, count ->
            if(anotherBuffer.size == vertexID)
                anotherBuffer.add()

            val vertexOffset = vertexID * buffer.componentCount
            val localOffset = srcOffset - vertexOffset

            when(count) {
                1 -> anotherBuffer.set(vertexID, localOffset, src[srcOffset])
                2 -> anotherBuffer.set(vertexID, localOffset, src[srcOffset], src[srcOffset + 1])
                else -> throw IllegalArgumentException("count greater than 2: $count")
            }
        }

        assertEquals(buffer, anotherBuffer)
    }

    @Test fun testGetBounds() {
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

        assertRectEquals(Rectangle(x, y, w, h), buffer.getBounds())

        val dx = 5f
        val dy = 7f

        val transform = Matrix3().translate(dx, dy)

        assertRectEquals(Rectangle(x + dx, y + dy, w, h), buffer.getBounds(0, transform))

        val scaleX = 0.333f
        val scaleY = 0.5f

        transform.scale(scaleX, scaleY)

        assertRectEquals(Rectangle(x * scaleX + dx, y * scaleY + dy, w * scaleX, h * scaleY), buffer.getBounds(0, transform))
    }

    @Test fun testTriangulate() {
        assertEquals(0, buffer.size)

        val x = 1f
        val y = 1f
        val w = 10f
        val h = 10f

        // vertices are created in the clockwise order
        buffer.add(4).
        set(0, 0, x, y).set(0, 2, Color.WHITE).set(0, 3, Vector2(0f, 0f)).
        set(1, 0, x + w, y).set(1, 2, Color.RED).set(1, 3, Vector2(1f, 0f)).
        set(2, 0, x + w, y + h).set(2, 2, Color.BLUE).set(2, 3, Vector2(1f, 1f)).
        set(3, 0, x, y + h).set(3, 2, Color.GREEN).set(3, 3, Vector2(0f, 1f));

        assertEquals(4, buffer.size)

        println(Indices.triangulate(buffer))
        println(buffer)
    }
}
