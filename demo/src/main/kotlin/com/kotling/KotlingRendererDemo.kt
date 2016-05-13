package com.kotling

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.kotling.rendering.*

class KotlingRendererDemo : ApplicationAdapter() {
    val coloredRenderer     = ColoredRenderer()
    val texturedRenderer    = object : TexturedRenderer() {
        @VertexFormat("position:float2", "color:float4", "texCoords:float2")
        override val attributes:VertexAttributes by VertexAttributesCache
    }

    lateinit var coloredVertices:Vertices
    lateinit var texturedVertices:Vertices
    lateinit var indices:Indices

    val camera = OrthographicCamera()

    override fun create() {
        coloredRenderer.globalColor = Color.FOREST

        texturedRenderer.globalColor = Color.CORAL
        texturedRenderer.globalColor.a = 0.6f

        val x = 50f
        val y = 50f
        val w = 180f
        val h = 180f

        coloredVertices = Vertices(coloredRenderer.attributes).
                add(4).
                set(0, 0, x, y).set(0, 2, Color.WHITE).
                set(1, 0, x + w, y).set(1, 2, Color.RED).
                set(2, 0, x + w, y + h).set(2, 2, Color.GREEN).
                set(3, 0, x, y + h).set(3, 2, Color.BLUE)

        texturedVertices = Vertices(texturedRenderer.attributes).
                add(4).
                set(0, 0, x + 200, y).set(0, 2, Color.WHITE, false).set(0, 6, 0f, 0f).
                set(1, 0, x + 200 + w, y).set(1, 2, Color.RED, false).set(1, 6, 1f, 0f).
                set(2, 0, x + 200 + w, y + h).set(2, 2, Color.BLUE, false).set(2, 6, 1f, 1f).
                set(3, 0, x + 200, y + h).set(3, 2, Color.GREEN, false).set(3, 6, 0f, 1f)

        texturedRenderer.texture = Texture("demo/assets/badlogic.jpg")

        indices = Indices.triangulate(coloredVertices)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        camera.setToOrtho(true, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        coloredRenderer.projection = camera.combined
        coloredRenderer.upload(coloredVertices, indices)
        coloredRenderer.render()

        texturedRenderer.projection = camera.combined
        texturedRenderer.upload(texturedVertices, indices)
        texturedRenderer.render()
    }
}
