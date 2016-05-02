package com.kotling.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Disposable

abstract class Renderer : Disposable {
    companion object {
        const val MIN_VERTICES:Int  = 32
        const val MIN_INDICES:Int   = 48
    }

    abstract val shader:ShaderProgram
    abstract val attributes:VertexAttributes

    var projection = Matrix4()
        set(value) { field.set(value) }

    protected var mesh:Mesh? = null

    /**
     * By default it does NOT dispose shader. Mesh is disposed however.
     */
    override fun dispose() {
        mesh?.dispose();
    }

    fun upload(vertices:Vertices, indices:Indices, vertexID:Int = 0, vertexCount:Int = vertices.size, indexID:Int = 0, indexCount:Int = indices.size) {
        if(mesh?.maxVertices ?: -1 < vertexCount || mesh?.maxIndices ?: -1 < indexCount)
            mesh = Mesh(true, true, Math.max(vertexCount, MIN_VERTICES), Math.max(indexCount, MIN_INDICES), attributes)

        mesh?.setVertices(vertices.rawData, vertexID * vertices.componentCount, vertexCount * vertices.componentCount)
        mesh?.setIndices(indices.rawData, indexID, indexCount)
    }

    fun render(firstIndexID:Int = 0, indexCount:Int = mesh?.numIndices ?: -1 - firstIndexID) {
        beforeDraw()
        mesh?.render(shader, GL20.GL_TRIANGLE_STRIP, firstIndexID, indexCount, true)
        afterDraw()
    }

    open protected fun beforeDraw() {
        shader.begin()
        shader.setUniformMatrix("projection", projection)
    }

    open protected fun afterDraw() {
        shader.end()
    }
}

open class ColoredRenderer() : Renderer() {
    @Shader("core/shaders/Colored")
    override val shader:ShaderProgram by ShaderProgramCache

    @VertexFormat("position:float2", "color:byte4")
    override val attributes:VertexAttributes by VertexAttributesCache

    var globalColor = Color.WHITE.cpy()
        set(value) { field.set(value) }

    override fun beforeDraw() {
        super.beforeDraw()

        shader.setUniformf("globalColor", globalColor)
    }
}

open class TexturedRenderer() : Renderer() {
    @Shader("core/shaders/Textured")
    override val shader:ShaderProgram by ShaderProgramCache

    @VertexFormat("position:float2", "color:byte4", "texCoords:float2")
    override val attributes:VertexAttributes by VertexAttributesCache

    var globalColor = Color.WHITE.cpy()
        set(value) { field.set(value) }

    lateinit var texture:Texture

    override fun beforeDraw() {
        super.beforeDraw()

        shader.setUniformf("globalColor", globalColor)

        Gdx.graphics.gL20.glActiveTexture(GL20.GL_TEXTURE0)
        texture.bind()
        shader.setUniformi("texture", 0)
    }
}
