package com.kotling

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.kotling.rendering.*

class KotlingDemo : ApplicationAdapter() {
    val coloredRenderer     = ColoredRenderer()
    val texturedRenderer    = TexturedRenderer()

    lateinit var coloredVertices:Vertices
    lateinit var texturedVertices:Vertices
    lateinit var indices:Indices

    val camera = OrthographicCamera()

    override fun create() {
        val x = 50f
        val y = 50f
        val w = 180f
        val h = 180f

        coloredVertices = Vertices(coloredRenderer.attributes).
                add(4).
                set(0, 0, x, y).set(0, 2, Color.WHITE).
                set(1, 0, x + w, y).set(1, 2, Color.RED).
                set(2, 0, x + w, y + h).set(2, 2, Color.BLUE).
                set(3, 0, x, y + h).set(3, 2, Color.GREEN)

        texturedVertices = Vertices(texturedRenderer.attributes).
                add(4).
                set(0, 0, x + 200, y).set(0, 2, Color.WHITE).set(0, 3, 0f, 0f).
                set(1, 0, x + 200 + w, y).set(1, 2, Color.RED).set(1, 3, 1f, 0f).
                set(2, 0, x + 200 + w, y + h).set(2, 2, Color.BLUE).set(2, 3, 1f, 1f).
                set(3, 0, x + 200, y + h).set(3, 2, Color.GREEN).set(3, 3, 0f, 1f)

        texturedRenderer.texture = Texture("demo/assets/badlogic.jpg")

        indices = Indices.triangulate(coloredVertices)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.setToOrtho(true, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        coloredRenderer.projection = camera.combined
        coloredRenderer.upload(coloredVertices, indices)
        coloredRenderer.render()

        texturedRenderer.projection = camera.combined
        texturedRenderer.upload(texturedVertices, indices)
        texturedRenderer.render()
    }

    class ColoredRenderer : Renderer() {
        @Shader("core/shaders/Colored")
        override val shader:ShaderProgram by ShaderProgramCache

        @VertexFormat("position:float2", "color:byte4")
        override val attributes:VertexAttributes by VertexAttributesCache
    }

    class TexturedRenderer : Renderer() {
        @Shader("core/shaders/Textured")
        override val shader:ShaderProgram by ShaderProgramCache

        @VertexFormat("position:float2", "color:byte4", "texCoords:float2")
        override val attributes:VertexAttributes by VertexAttributesCache

        lateinit var texture:Texture

        override fun beforeDraw() {
            super.beforeDraw()

            Gdx.graphics.gL20.glActiveTexture(GL20.GL_TEXTURE0)
            texture.bind(0)
            shader.setUniformi("texture", 0)
        }
    }
}
