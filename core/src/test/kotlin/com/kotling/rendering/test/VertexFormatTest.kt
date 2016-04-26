package com.kotling.rendering.test

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import junit.framework.TestCase
import kotlin.test.assertNotEquals

class VertexFormatTest : TestCase() {
    fun testNoFormatException() {
        val subject = ShouldBlowUpSubject()
        try {
            subject.attributes.size()
            fail("UninitializedPropertyAccessException not thrown")
        }
        catch(e:UninitializedPropertyAccessException) {
            assertEquals("property not annotated with @VertexFormat", e.message)
        }
    }

    fun testFormat() {
        val subjectA = TestSubjectA()
        assertEquals(3, subjectA.attributes.size())

        val subjectB = TestSubjectB()
        assertEquals(subjectA.attributes, subjectB.attributes)

        val subjectC = TestSubjectC()
        assertNotEquals(subjectA.attributes, subjectC.attributes)
    }
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

class TestSubjectC {
    @VertexFormat("position:float2", "uv:float2", "color:byte4")
    val attributes:VertexAttributes by VertexAttributesCache
}
