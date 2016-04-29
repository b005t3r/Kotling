package com.kotling.rendering.test

import com.kotling.rendering.Indices
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class IndicesTest {
    lateinit var buffer:Indices

    @Before fun setUp() {
        buffer = Indices()
    }

    @Test fun testAddIndex() {
        assertEquals(0, buffer.size)

        buffer.add(0)
        assertEquals(1, buffer.size)
        assertEquals(0, buffer[buffer.size - 1])

        buffer.add(5)
        assertEquals(2, buffer.size)
        assertEquals(5, buffer[buffer.size - 1])
    }

    @Test fun testAddTriangle() {
        assertEquals(0, buffer.size)

        buffer.add(1, 2, 3);
        buffer.add(2, 3, 4);

        assertEquals(6, buffer.size)
        assertEquals(1, buffer[0])
        assertEquals(2, buffer[1])
        assertEquals(3, buffer[2])
        assertEquals(2, buffer[3])
        assertEquals(3, buffer[4])
        assertEquals(4, buffer[5])
    }

    @Test fun testCopyTo() {
        buffer.add(1, 2, 3);
        buffer.add(2, 4, 3);
        buffer.add(5, 6, 7);
        buffer.add(6, 8, 7);

        val other = Indices(0)

        assertEquals(0, other.size)

        buffer.copyTo(other)

        assertEquals(buffer, other)
    }

    @Test fun testClone() {
        buffer.add(1, 2, 3);
        buffer.add(2, 4, 3);
        buffer.add(5, 6, 7);
        buffer.add(6, 8, 7);

        val clone = buffer.clone()

        assertEquals(buffer, clone)
        assert(buffer !== clone)
    }

    @Test fun testClear() {
        assertEquals(0, buffer.size)

        buffer.add(1, 2, 3);
        buffer.add(2, 4, 3);
        buffer.add(5, 6, 7);
        buffer.add(6, 8, 7);

        assertEquals(12, buffer.size)

        buffer.clear()

        assertEquals(0, buffer.size)

        buffer.add(1, 2, 3);
        buffer.add(2, 4, 3);
        buffer.add(5, 6, 7);
        buffer.add(6, 8, 7);

        assertEquals(12, buffer.size)

        buffer.clear()

        assertEquals(0, buffer.size)
    }
}
