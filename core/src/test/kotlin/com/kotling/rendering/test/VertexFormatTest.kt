package com.kotling.rendering.test

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import junit.framework.TestCase

class VertexFormatTest : TestCase() {
    fun testFormat() {
        val test = Test()
        var attrs = test.attributes
        assertEquals(3, test.attributes.size())
    }
}

class Test {
    @VertexFormat("position:float2", "color:byte4", "uv:float2")
    val attributes:VertexAttributes by VertexAttributesCache
}