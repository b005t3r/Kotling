package com.kotling.rendering

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Matrix3
import com.kotling.util.poolable.use
import com.kotling.util.Pool

class Vertices(val attributes:VertexAttributes, initialCapacity:Int = 32) {
    companion object {
        const val MIN_CAPACITY:Int = 32
    }

    init {
        for(attr in attributes)
            if(attr.usage == VertexAttributes.Usage.ColorPacked)
                ++componentCount
            else
                componentCount += attr.numComponents
    }

    var componentCount = 0
        private set

    var positionOffset = -1
        private set

    val componentOffsets:Map<String, Int> by lazy(LazyThreadSafetyMode.NONE, fun():Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        var offset = 0

        for(attr in attributes) {
            map[attr.alias] = offset

            if(positionOffset < 0 && attr.usage == VertexAttributes.Usage.Position)
                positionOffset = offset

            if(attr.usage == VertexAttributes.Usage.ColorPacked)
                ++offset
            else
                offset += attr.numComponents
        }

        return map
    })

    var size = 0
        private set

    var rawData = FloatArray(Math.max(MIN_CAPACITY, initialCapacity * componentCount))
        private set

    fun clear(trim:Boolean = false) {
        size = 0

        if(trim)
            trim()
    }

    fun trim() {
        if(rawData.size > MIN_CAPACITY)
            rawData = FloatArray(Math.max(size, MIN_CAPACITY), { i -> rawData[i] })
    }

    fun ensureCapacity(newCapacity:Int) {
        if(rawData.size >= newCapacity * componentCount)
            return

        val newSize = ((newCapacity / MIN_CAPACITY) + 1) * MIN_CAPACITY * componentCount
        rawData = FloatArray(newSize, { i -> if(i < rawData.size) rawData[i] else Float.NaN });
    }

    fun copyTo(target:Vertices, targetVertexID:Int = 0, matrix:Matrix3? = null, vertexID:Int = 0, count:Int = -1) {
        if(targetVertexID !in 0..target.size)
            throw IndexOutOfBoundsException("targetIndexID $targetVertexID is outside 0..${target.size - 1}")

        if(attributes != target.attributes)
            throw IllegalArgumentException("target's attributes don't match, attributes: $attributes, target.attributes: ${target.attributes}")

        if(size == 0)
            return

        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val numVertices = if(count < 0 || vertexID + count > size) size - vertexID else count
        val newSize = targetVertexID + numVertices

        target.ensureCapacity(newSize)

        val components = 0..componentCount - 1
        if(matrix == null) {
            for(i in 0..newSize - 1)
                for(c in components)
                    target.rawData[targetVertexID + i + c] = rawData[vertexID + i + c]
        }
        else {
            Pool.Vector2.use { p ->
                for(i in 0..newSize - 1) {
                    var c = 0
                    while(c in components ) {
                        if(c != positionOffset) {
                            target.rawData[targetVertexID + i + c] = rawData[vertexID + i + c]
                        }
                        else {
                            p.set(rawData[vertexID + i + c], rawData[vertexID + i + c + 1]).mul(matrix)
                            target.rawData[targetVertexID + i + c]      = p.x
                            target.rawData[targetVertexID + i + c + 1]  = p.y
                            ++c
                        }

                        ++c
                    }
                }
            }
        }

        target.size = newSize
    }

    inline fun copyTo(target:Vertices, targetVertexID:Int = 0, vertexID:Int = 0, count:Int = -1, block : (vertexID:Int, targetVertexID:Int, attrID:Int, src:FloatArray, srcOffset:Int, dst:FloatArray, dstOffset:Int, count:Int) -> Unit) {
        if(targetVertexID !in 0..target.size)
            throw IndexOutOfBoundsException("targetIndexID $targetVertexID is outside 0..${target.size - 1}")

        if(attributes != target.attributes)
            throw IllegalArgumentException("target's attributes don't match, attributes: $attributes, target.attributes: ${target.attributes}")

        if(size == 0)
            return

        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val numVertices = if(count < 0 || vertexID + count > size) size - vertexID else count
        val newSize = targetVertexID + numVertices

        target.ensureCapacity(newSize)

        var attrsWithIndices = attributes.withIndex()

        var srcOffset = vertexID * componentCount
        var dstOffset = targetVertexID * componentCount
        for(i in 0..newSize - 1) {
            for((attrID, attr) in attrsWithIndices) {
                if(attr.usage == VertexAttributes.Usage.ColorPacked) {
                    block(vertexID + i, targetVertexID + i, attrID, rawData, srcOffset, target.rawData, dstOffset, 1)
                    ++srcOffset
                    ++dstOffset
                }
                else {
                    block(vertexID + i, targetVertexID + i, attrID, rawData, srcOffset, target.rawData, dstOffset, attr.numComponents)
                    srcOffset += attr.numComponents
                    dstOffset += attr.numComponents
                }
            }
        }
    }
}
