package com.kotling.display.mesh

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Matrix3
import com.kotling.rendering.Indices
import com.kotling.rendering.Painter
import com.kotling.rendering.Renderer
import com.kotling.rendering.Vertices
import com.kotling.style.Style
import com.kotling.util.Pool
import com.kotling.util.poolable.use
import com.kotling.util.set

class MeshBatch(attributes:VertexAttributes) : MeshDisplay(Vertices(attributes), Indices(), Style.createStyle(attributes)) {
    constructor(mesh:MeshDisplay) : this(mesh.vertices.attributes)

    var renderer:Renderer? = null
        private set

    var batchable:Boolean = false
        set(value) {
            if(value == field)
                return

            field = value
            requiresRedraw = true
        }

    private var uploadRequired = false

    override fun dispose() {
        renderer?.dispose()

        super.dispose()
    }

    fun clear() {
        vertices.clear()
        indices.clear()
        uploadRequired = true
    }

    fun canAddMesh(mesh:MeshDisplay, vertexCount:Int = mesh.vertices.size):Boolean = vertices.size + vertexCount <= Vertices.MAX_CAPACITY && vertices.size + vertexCount > Vertices.MAX_CAPACITY

    fun add(mesh:MeshDisplay, matrix:Matrix3 = mesh.transformationMatrix, color:Color = Color.WHITE, vertexID:Int = 0, vertexCount:Int = mesh.vertices.size - vertexID, indexID:Int = 0, indexCount:Int = mesh.indices.size - indexID, ignoreTransform:Boolean = false) {
        val targetVertexID = vertices.size

        if(color == Color.WHITE) {
            mesh.style.vertices.copyTo(vertices, targetVertexID, if(! ignoreTransform) matrix else null, vertexID, vertexCount)
        }
        else {
            val posAttrID = mesh.style.vertices.positionAttrID
            val colorAttrID = mesh.style.attributes.indexOfFirst {
                attr -> when(attr.usage) {
                    VertexAttributes.Usage.ColorPacked -> true
                    VertexAttributes.Usage.ColorUnpacked -> true
                    else -> false
                }
            }

            if(colorAttrID < 0)
                throw IllegalStateException("no color attribute defined for vertices: ${mesh.style.vertices}")

            val packedColor = mesh.style.vertices.attributes[colorAttrID].usage == VertexAttributes.Usage.ColorPacked

            Pool.Color.use { c ->
            Pool.Vector2.use { p ->
                mesh.style.vertices.copyTo(vertices, targetVertexID, vertexID, vertexCount) {
                    vertexID:Int, targetVertexID:Int, attrID:Int, src:FloatArray, srcOffset:Int, dst:FloatArray, dstOffset:Int, count:Int ->
                    if(attrID == posAttrID) {
                        p.set(src[srcOffset], src[srcOffset + 1])
                        if(! ignoreTransform)
                            p.mul(matrix)

                        dst[dstOffset]      = p.x
                        dst[dstOffset + 1]  = p.y
                    }
                    else if(attrID == colorAttrID) {
                        if(packedColor) {
                            c.set(src[srcOffset]).mul(color)
                            dst[dstOffset] = c.toFloatBits()
                        }
                        else {
                            c.set(src[srcOffset], src[srcOffset + 1], src[srcOffset + 2], src[srcOffset + 3]).mul(color)
                            dst[dstOffset]      = c.r
                            dst[dstOffset + 1]  = c.g
                            dst[dstOffset + 2]  = c.b
                            dst[dstOffset + 3]  = c.a
                        }
                    }
                    else {
                        for(i in 0..count - 1)
                            dst[dstOffset + i] = src[srcOffset + i]
                    }
                }
            }}
        }

        mesh.style.indices.copyTo(indices, indices.size, targetVertexID.toShort(), indexID, indexCount)

        if(batchable)
            requiresRedraw = true

        uploadRequired = true
    }

    override fun render(painter:Painter) {
        if(vertices.size == 0)
            return

        // TODO: pixel snapping
        //if(pixelSnappingEnabled)
        //    painter.state.modelViewMatrix.snapToPixels(painter.pixelSize)

        if(batchable) {
            painter.batchMesh(this)
            return
        }

        painter.finishBatch()
        painter.drawCount += 1
        painter.prepareToDraw()
        painter.excludeFromCache(this)

        if(uploadRequired)
            upload()

        if(renderer == null)
            renderer = style.createRenderer()

        if(renderer != null) {
            style.updateRenderer(renderer!!, painter.state)
            renderer!!.render(0, indices.size)
        }
    }

    private fun upload() = renderer?.upload(vertices, indices)
}
