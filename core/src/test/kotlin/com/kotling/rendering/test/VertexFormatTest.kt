package com.kotling.rendering.test

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class VertexFormatTest {
    @Test fun testNoFormatException() {
        val subject = ShouldBlowUpSubject()
        try {
            subject.attributes.size()
            fail("UninitializedPropertyAccessException not thrown")
        }
        catch(e:UninitializedPropertyAccessException) {
            assertEquals("property not annotated with @VertexFormat", e.message)
        }
    }

    @Test fun testFormat() {
        val subjectA = TestSubjectA()
        assertEquals(3, subjectA.attributes.size())

        val subjectB = TestSubjectB()
        assertEquals(subjectA.attributes, subjectB.attributes)

        val subjectC = TestSubjectC()
        assertNotEquals(subjectA.attributes, subjectC.attributes)

        val subjectD = TestSubjectD()
        assertNotEquals(subjectC.attributes, subjectD.attributes)
        assertEquals(subjectA.attributes, subjectD.attributes)
        assertEquals(subjectB.attributes, subjectD.attributes)
    }

    class ShouldBlowUpSubject {
        val attributes:VertexAttributes by VertexAttributesCache
    }

    class TestSubjectA {
        @VertexFormat("position:float2", "color:byte4", "uv:float2")
        val attributes:VertexAttributes by VertexAttributesCache
    }

    class TestSubjectB {
        @VertexFormat("position:float2", "color:byte4", "uv:float2")
        val attributes:VertexAttributes by VertexAttributesCache
    }

    open class TestSubjectC {
        @VertexFormat("position:float2", "uv:float2", "color:byte4")
        open val attributes:VertexAttributes by VertexAttributesCache
    }

    class TestSubjectD : TestSubjectC() {
        @VertexFormat("position:float2", "color:byte4", "uv:float2")
        override val attributes:VertexAttributes by VertexAttributesCache
    }
}
