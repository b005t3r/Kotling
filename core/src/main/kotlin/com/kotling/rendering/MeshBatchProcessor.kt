package com.kotling.rendering

import com.badlogic.gdx.utils.Disposable
import com.kotling.display.mesh.MeshBatch
import com.kotling.display.mesh.MeshDisplay
import com.kotling.style.Style
import com.kotling.util.Pool
import com.kotling.util.from3D
import com.kotling.util.poolable.use

internal class MeshBatchProcessor : Disposable {
    val onBatchComplete:((MeshBatch)->Unit)? = null
    val size:Int
        get() = batches.size

    private val batches = mutableListOf<MeshBatch>()
    private val pool = MeshBatchPool()
    private var currentBatch:MeshBatch? = null

    private var cacheToken = BatchToken()

    override fun dispose() {
        batches.forEach { it.dispose() }
        batches.clear()
        pool.purge()
    }

    fun add(mesh:MeshDisplay, state:RenderState? = null, vertexID:Int = 0, vertexCount:Int = mesh.vertices.size - vertexID, indexID:Int = 0, indexCount:Int = mesh.indices.size - indexID, ignoreTransform:Boolean = false) {
        if(vertexCount == 0)
            return

        if(indexCount == 0)
            throw IllegalArgumentException("no indices, but vertices present")

        if(currentBatch == null || ! currentBatch!!.canAddMesh(mesh, vertexCount)) {
            finishBatch()

            currentBatch = pool.get(mesh.style)
            batches += currentBatch!!

            currentBatch!!.blendMode = state?.blendMode ?: mesh.blendMode
            cacheToken.set(batches.lastIndex)
        }

        if(state != null)
            currentBatch!!.add(mesh, state.modelViewMatrix, state.color, vertexID, vertexCount, indexID, indexCount, ignoreTransform)
        else
            currentBatch!!.add(mesh, vertexID = vertexID, vertexCount = vertexCount, indexID = indexID, indexCount = indexCount, ignoreTransform = ignoreTransform)

        cacheToken.vertexID += vertexCount
        cacheToken.indexID += indexCount
    }

    fun finishBatch() {
        val batch = currentBatch ?: return

        currentBatch = null
        onBatchComplete?.invoke(batch)
    }

    fun clear() {
        batches.forEach { pool.put(it) }
        batches.clear()
        currentBatch = null
        cacheToken.reset()
    }

    operator fun get(batchID:Int):MeshBatch = batches[batchID]
    fun trim() = pool.purge()

    fun rewindTo(token:BatchToken) {
        if(token.batchID > cacheToken.batchID)
            throw IllegalArgumentException("token.batchID (${token.batchID}) greater than the cacheToken.batchID (${cacheToken.batchID})")

        for(i in cacheToken.batchID downTo token.batchID + 1)
            pool.put(batches.removeAt(i))

        // is this if really necessary?
        if(batches.size > token.batchID) {
            val batch = batches[token.batchID]

            if(token.vertexID < batch.vertices.size)
                batch.vertices.retainFirst(token.vertexID)
            
            if(token.vertexID < batch.indices.size)
                batch.indices.retainFirst(token.vertexID)
        }

        currentBatch = null
        cacheToken.set(token)
    }

    fun fillToken(token:BatchToken):BatchToken = token.set(cacheToken)


}

internal class  MeshBatchPool {
    private val batchLists = mutableMapOf<Class<Style>, MutableList<MeshBatch>>()

    fun purge() {
        batchLists.values.forEach {
            it.forEach { it.dispose() }
        }

        batchLists.clear()
    }

    fun get(style:Style):MeshBatch {
        var batches = batchLists[style.javaClass]
        if(batches == null) {
            batches = mutableListOf()
            batchLists[style.javaClass] = batches
        }

        return if(! batches.isEmpty()) batches.removeAt(batches.lastIndex) else MeshBatch(style.attributes)
    }

    fun put(batch:MeshBatch) {
        var batches = batchLists[batch.style.javaClass]
        if(batches == null) {
            batches = mutableListOf()
            batchLists[batch.style.javaClass] = batches
        }

        batch.clear()
        batches.add(batch)
    }
}
