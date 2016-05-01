package com.kotling.rendering

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttributes
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
