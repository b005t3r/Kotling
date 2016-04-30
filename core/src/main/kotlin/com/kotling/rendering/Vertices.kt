package com.kotling.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.kotling.util.Pool
import com.kotling.util.fromFloatBits
import com.kotling.util.poolable.use

class Vertices(val attributes:VertexAttributes, initialCapacity:Int = 32) {
    companion object {
        const val MIN_CAPACITY:Int = 4
    }

    init {
        var offset = 0

        for(attr in attributes) {
            if(positionOffset < 0) {
                if(attr.usage == VertexAttributes.Usage.Position)
                    positionOffset = offset
                else if(attr.usage == VertexAttributes.Usage.ColorPacked)
                    ++offset
                else
                    offset += attr.numComponents
            }

            if(attr.usage == VertexAttributes.Usage.ColorPacked)
                ++componentCount
            else
                componentCount += attr.numComponents
        }
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
        if(rawData.size > MIN_CAPACITY * componentCount)
            rawData = FloatArray(Math.max(size * componentCount, MIN_CAPACITY * componentCount), { i -> rawData[i] })
    }

    fun ensureCapacity(newCapacity:Int):Vertices {
        if(rawData.size >= newCapacity * componentCount)
            return this

        val newSize = ((newCapacity / MIN_CAPACITY) + 1) * MIN_CAPACITY * componentCount
        rawData = FloatArray(newSize, { i -> if(i < rawData.size) rawData[i] else Float.NaN });

        return this
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

    fun add():Vertices = ensureCapacity(++size)

    fun add(count:Int):Vertices {
        size += count
        ensureCapacity(size)

        return this
    }

    fun set(vertexID:Int, offset:Int, value:Float):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        rawData[vertexID * componentCount + offset] = value

        return this
    }

    fun set(vertexID:Int, offset:Int, x:Float, y:Float):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset]        = x
        rawData[totalOffset + 1]    = y

        return this
    }

    fun set(vertexID:Int, offset:Int, p:Vector2):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset]        = p.x
        rawData[totalOffset + 1]    = p.y

        return this
    }

    fun set(vertexID:Int, offset:Int, x:Float, y:Float, z:Float):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset]        = x
        rawData[totalOffset + 1]    = y
        rawData[totalOffset + 2]    = z

        return this
    }

    fun set(vertexID:Int, offset:Int, p:Vector3):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset]        = p.x
        rawData[totalOffset + 1]    = p.y
        rawData[totalOffset + 2]    = p.z

        return this
    }

    fun set(vertexID:Int, offset:Int, x:Float, y:Float, z:Float, w:Float):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset]        = x
        rawData[totalOffset + 1]    = y
        rawData[totalOffset + 2]    = z
        rawData[totalOffset + 3]    = w

        return this
    }

    fun set(vertexID:Int, offset:Int, c:Color):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        rawData[totalOffset] = c.toFloatBits()

        return this
    }

    fun get(vertexID:Int, offset:Int):Float {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        return rawData[vertexID * componentCount + offset]
    }

    fun get(vertexID:Int, offset:Int, result:Vector2? = null):Vector2 {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val out = result ?: Vector2()

        val totalOffset = vertexID * componentCount + offset
        return out.set(rawData[totalOffset], rawData[totalOffset + 1])
    }

    fun get(vertexID:Int, offset:Int, result:Vector3? = null):Vector3 {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val out = result ?: Vector3()

        val totalOffset = vertexID * componentCount + offset
        return out.set(rawData[totalOffset], rawData[totalOffset + 1], rawData[totalOffset + 2])
    }

    fun get(vertexID:Int, offset:Int, result:FloatArray? = null):FloatArray {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        if(result != null && result.size < 4)
            throw IllegalArgumentException("result has to be at least of size 4 or null (current size: ${result.size})")

        val out = result ?: FloatArray(4)

        val totalOffset = vertexID * componentCount + offset
        out[0] = rawData[totalOffset]
        out[1] = rawData[totalOffset + 1]
        out[2] = rawData[totalOffset + 2]
        out[3] = rawData[totalOffset + 3]

        return out
    }

    fun get(vertexID:Int, offset:Int, result:Color? = null):Color {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val out = result ?: Color()

        val totalOffset = vertexID * componentCount + offset

        return out.set(rawData[totalOffset].fromFloatBits())
    }
}
