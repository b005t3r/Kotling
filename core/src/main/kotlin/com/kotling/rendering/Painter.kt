package com.kotling.rendering

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.kotling.display.BlendMode
import com.kotling.display.Display
import com.kotling.display.mesh.MeshBatch
import com.kotling.display.mesh.MeshDisplay
import com.kotling.util.Pool
import com.kotling.util.poolable.PoolableRectangle

class Painter {
    var drawCount = 0
        internal set

    var frameID = 0
        private set

    var pixelSize = 1f
        private set

    private val clipRectStack           = mutableListOf<PoolableRectangle>()

    private var batchProcessor          = MeshBatchProcessor()
    private var batchCache              = MeshBatchProcessor()
    private val batchCacheExclusions    = mutableListOf<Display>()

    private var actualRenderTarget:FrameBuffer? = null
    private var actualBlendMode:BlendMode?      = null

    val state                           = RenderState()
    private val stateStack              = mutableListOf<RenderState>()

    fun pushState(token:BatchToken? = null) {
        stateStack.add(Pool.get<RenderState>().obtain())

        if(token != null)
            batchProcessor.fillToken(token)

        stateStack.last().set(state)
    }

    fun setStateTo(transformationMatrix:Matrix3, globalColor:Color = Color.WHITE, blendMode:BlendMode = BlendMode.AUTO) {
        state.modelViewMatrix.mulLeft(transformationMatrix)

        if(globalColor != Color.WHITE)
            state.color.mul(globalColor)

        if(blendMode != BlendMode.AUTO)
            state.blendMode = blendMode
    }

    fun popState(token:BatchToken? = null) {
        if(stateStack.isEmpty())
            throw IllegalStateException("state stack is empty, nothing to pop")

        state.set(stateStack.last()) // might trigger finishMeshBatch
        Pool.get<RenderState>().free(stateStack.removeAt(stateStack.lastIndex))

        if(token != null)
            batchProcessor.fillToken(token)
    }

    // TODO: mask rendering

    fun pushClipRect(clipRect:Rectangle) {
        if(clipRectStack.isEmpty()) {
            clipRectStack.add(Pool.Rectangle.obtain())
            clipRectStack.last().set(clipRect)
        }
        else {
            val result = Pool.Rectangle.obtain()
            Intersector.intersectRectangles(clipRect, clipRectStack.last(), result)
            clipRectStack.add(result)
        }

        state.clipRect = clipRectStack.last()
    }

    fun popClipRect() {
        if(clipRectStack.isEmpty())
            throw IllegalStateException("clip rect stack is empty, nothing to pop")

        Pool.Rectangle.free(clipRectStack.removeAt(clipRectStack.lastIndex))
        state.clipRect = if(! clipRectStack.isEmpty()) clipRectStack.last() else null
    }

    fun batchMesh(mesh:MeshDisplay, vertexID:Int = 0, vertexCount:Int = mesh.vertices.size - vertexID, indexID:Int = 0, indexCount:Int = mesh.indices.size - indexID) = batchProcessor.add(mesh, state, vertexID, vertexCount, indexID, indexCount)
    fun finishBatch() = batchProcessor.finishBatch()

    fun finishFrame() {
        if(frameID % 99 == 0)
            batchProcessor.trim()

        batchProcessor.finishBatch()
        swapBatchProcessors()
        batchProcessor.clear()
        processCacheExclusions()
    }

    fun nextFrame() {
        actualBlendMode = null
        actualRenderTarget = null

        clipRectStack.forEach { Pool.Rectangle.free(it) }
        clipRectStack.clear()
        drawCount = 0
        batchProcessor.clear()
        state.reset()
    }

    fun drawFromCache(startToken:BatchToken, endToken:BatchToken) {
        if(startToken == endToken)
            return

        pushState()

        for(id in startToken.batchID..endToken.batchID) {
            val meshBatch = batchCache[id]
            var vertexID        = 0
            var vertexCount     = meshBatch.vertices.size
            var indexID:Int     = 0
            var indexCount:Int  = meshBatch.indices.size

            if(id == startToken.batchID) {
                vertexID    = startToken.vertexID
                indexID     = startToken.indexID
                vertexCount = meshBatch.vertices.size - vertexID
                indexCount  = meshBatch.indices.size - indexID
            }
            else if(id == endToken.batchID) {
                vertexCount = endToken.vertexID - vertexID
                indexCount  = endToken.indexID - indexID
            }

            if(vertexCount > 0) {
                state.color     = Color.WHITE
                state.blendMode = meshBatch.blendMode
                batchProcessor.add(meshBatch, state, vertexID, vertexCount, indexID, indexCount, true)
            }
        }

        popState()
    }

    fun rewindCacheTo(token:BatchToken) = batchProcessor.rewindTo(token)

    fun excludeFromCache(display:Display) = batchCacheExclusions.add(display)

    fun prepareToDraw() {
        applyBlendMode()
        applyRenderTarget()
        applyClipRect()
    }

    fun clear(color:Color = Color.BLACK) {
        applyRenderTarget()

        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    fun present() {
        state.renderTarget = null
        actualRenderTarget = null

        //Gdx.gl.glFlush()
    }

    private fun swapBatchProcessors() {
        val tmp = batchProcessor
        batchProcessor = batchCache
        batchCache = tmp
    }

    private fun processCacheExclusions() {
        batchCacheExclusions.forEach { it.excludeFromCache() }
        batchCacheExclusions.clear()
    }

    private fun drawBatch(meshBatch:MeshBatch) {
        pushState()

        state.blendMode = meshBatch.blendMode
        state.modelViewMatrix.idt()
        state.color = Color.WHITE

        meshBatch.render(this)

        popState()
    }

    private fun applyBlendMode() {
        if(actualBlendMode == state.blendMode)
            return

        actualBlendMode = state.blendMode
        actualBlendMode!!.activate()
    }

    private fun applyRenderTarget() {
        if(actualRenderTarget == state.renderTarget)
            return

        // TODO: handling render targets
        //if(state.renderTarget != null)
            //state.renderTarget!!.bind()
        //else
            // coś innego

        actualRenderTarget = state.renderTarget
    }

    private fun applyClipRect() {

    }
}
