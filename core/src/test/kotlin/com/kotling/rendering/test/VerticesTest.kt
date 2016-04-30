package com.kotling.rendering.test

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import com.kotling.rendering.Vertices
import org.junit.Before
import org.junit.Test

class VerticesTest {
    lateinit var buffer:Vertices

    @VertexFormat("position:float2", "color:byte4", "uv:float2")
    val testAttributes:VertexAttributes by VertexAttributesCache

    @Before fun setUp() {
        buffer = Vertices(testAttributes)
    }

    @Test fun testAdd() {

    }
}