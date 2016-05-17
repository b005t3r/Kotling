package com.kotling.rendering

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.*
import com.kotling.util.Pool
import com.kotling.util.poolable.use
import com.kotling.util.set

class Vertices(val attributes:VertexAttributes, initialCapacity:Int = MIN_CAPACITY) : Cloneable {
    companion object {
        const val MIN_CAPACITY:Int = 4
        const val MAX_CAPACITY:Int = Short.MAX_VALUE.toInt()
    }

    init {
        var offset = 0

        for((attrID, attr) in attributes.withIndex()) {
            if(positionOffset < 0) {
                if(attr.usage == VertexAttributes.Usage.Position) {
                    positionOffset = offset
                    positionAttrID = attrID
                }
                else if(attr.usage == VertexAttributes.Usage.ColorPacked) {
                    ++offset
                }
                else {
                    offset += attr.numComponents
                }
            }

            if(attr.usage == VertexAttributes.Usage.ColorPacked)
                ++componentCount
            else
                componentCount += attr.numComponents

            if(positionOffset < 0)
                throw IllegalArgumentException("position not defined in attributes: $attributes")
        }
    }

    var componentCount = 0
        private set

    var positionOffset = -1
        private set

    var positionAttrID = -1
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

    var rawData = FloatArray(Math.max(MIN_CAPACITY, initialCapacity) * componentCount) { i -> Float.NaN }
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

    /** Retain <code>count</code> first vertices (just changes the size internally). */
    fun retainFirst(count:Int) = if(count < size) size = count else throw IllegalArgumentException("count ($count) is greater than size ($size)")

    fun ensureCapacity(newCapacity:Int):Vertices {
        if(rawData.size >= newCapacity * componentCount)
            return this

        val newSize = ((newCapacity / MIN_CAPACITY) + 1) * MIN_CAPACITY * componentCount
        rawData = FloatArray(newSize) { i -> if(i < rawData.size) rawData[i] else Float.NaN }

        return this
    }

    fun copyTo(target:Vertices, targetVertexID:Int = 0, matrix:Matrix3? = null, vertexID:Int = 0, count:Int = size - vertexID) {
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

    inline fun copyTo(target:Vertices, targetVertexID:Int = 0, vertexID:Int = 0, count:Int = size - vertexID, block : (vertexID:Int, targetVertexID:Int, attrID:Int, src:FloatArray, srcOffset:Int, dst:FloatArray, dstOffset:Int, count:Int) -> Unit) {
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

        var srcOffset = vertexID * componentCount
        var dstOffset = targetVertexID * componentCount
        for(i in 0..newSize - 1) {
            for((attrID, attr) in attributes.withIndex()) {
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

        target.size = newSize
    }

    inline fun forEach(vertexID:Int = 0, count:Int = size - vertexID, block : (vertexID:Int, attrID:Int, src:FloatArray, srcOffset:Int, count:Int) -> Unit) {
        if(size == 0)
            return

        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val numVertices = if(count < 0 || vertexID + count > size) size - vertexID else count

        var srcOffset = vertexID * componentCount
        for(i in 0..numVertices - 1) {
            for((attrID, attr) in attributes.withIndex()) {
                if(attr.usage == VertexAttributes.Usage.ColorPacked) {
                    block(vertexID + i, attrID, rawData, srcOffset, 1)
                    ++srcOffset
                }
                else {
                    block(vertexID + i, attrID, rawData, srcOffset, attr.numComponents)
                    srcOffset += attr.numComponents
                }
            }
        }
    }

    fun getBounds(vertexID:Int = 0, matrix:Matrix3? = null, count:Int = size - vertexID, result:Rectangle? = null):Rectangle {
        val out = result ?: Rectangle()

        val numVertices = if(count < 0 || vertexID + count > size) size - vertexID else count

        if(numVertices == 0) {
            if(matrix == null)
                return out.set(0f, 0f, 0f, 0f)

            Pool.Vector2.use {
                it.set(0f, 0f).mul(matrix)
                return out.setPosition(it).setSize(0f, 0f)
            }
        }

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        var srcOffset = vertexID * componentCount

        if(matrix == null) {
            for(i in 0..numVertices - 1) {
                for(attr in attributes) {
                    if(attr.usage == VertexAttributes.Usage.Position) {
                        minX = Math.min(minX, rawData[srcOffset])
                        minY = Math.min(minY, rawData[srcOffset + 1])
                        maxX = Math.max(maxX, rawData[srcOffset])
                        maxY = Math.max(maxY, rawData[srcOffset + 1])
                    }

                    if(attr.usage == VertexAttributes.Usage.ColorPacked)
                        ++srcOffset
                    else
                        srcOffset += attr.numComponents
                }
            }
        }
        else {
            Pool.Vector2.use {
                for(i in 0..numVertices - 1) {
                    for(attr in attributes) {
                        if(attr.usage == VertexAttributes.Usage.Position) {
                            it.set(rawData[srcOffset], rawData[srcOffset + 1])

                            it.mul(matrix)

                            minX = Math.min(minX, it.x)
                            minY = Math.min(minY, it.y)
                            maxX = Math.max(maxX, it.x)
                            maxY = Math.max(maxY, it.y)
                        }

                        if(attr.usage == VertexAttributes.Usage.ColorPacked)
                            ++srcOffset
                        else
                            srcOffset += attr.numComponents
                    }
                }
            }
        }

        return out.set(minX, minY, maxX - minX, maxY - minY)
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

    /**
     * Because of how values are packed internally, alpha 0x00 and alpha 0x01 are both treated as 0x00.
     * Other alpha values are unaffected.
     */
    fun set(vertexID:Int, offset:Int, c:Color, packed:Boolean = true):Vertices {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val totalOffset = vertexID * componentCount + offset
        if(packed) {
            rawData[totalOffset] = c.toFloatBits()
        }
        else {
            rawData[totalOffset]        = c.r
            rawData[totalOffset + 1]    = c.g
            rawData[totalOffset + 2]    = c.b
            rawData[totalOffset + 3]    = c.a
        }

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

    /**
     * @see set
     */
    fun get(vertexID:Int, offset:Int, packed:Boolean = true, result:Color? = null):Color {
        if(vertexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $vertexID is outside 0..${size - 1}")

        val out = result ?: Color()

        val totalOffset = vertexID * componentCount + offset

        return when(packed) {
            true -> out.set(rawData[totalOffset])
            false -> out.set(rawData[totalOffset], rawData[totalOffset + 1], rawData[totalOffset + 2], rawData[totalOffset + 3])
        }
    }

    fun contains(point:Vector2, firstVertexID:Int, secondVertexID:Int, thirdVertexID:Int):Boolean {
        Pool.Vector2.use { firstVertex ->
        Pool.Vector2.use { secondVertex ->
        Pool.Vector2.use { thirdVertex ->
            get(firstVertexID, positionOffset, firstVertex)
            get(secondVertexID, positionOffset, secondVertex)
            get(thirdVertexID, positionOffset, thirdVertex)

            return Intersector.isPointInTriangle(point, firstVertex, secondVertex,thirdVertex)
        }}}
    }

    fun contains(point:Vector2, indices:Indices):Boolean {
        Pool.Vector2.use { firstVertex ->
        Pool.Vector2.use { secondVertex ->
        Pool.Vector2.use { thirdVertex ->
            for(i in 0..indices.size - 1 step 3) {
                val firstVertexID   = indices[i].toInt()
                val secondVertexID  = indices[i + 1].toInt()
                val thirdVertexID   = indices[i + 2].toInt()

                get(firstVertexID, positionOffset, firstVertex)
                get(secondVertexID, positionOffset, secondVertex)
                get(thirdVertexID, positionOffset, thirdVertex)

                if(Intersector.isPointInTriangle(point, firstVertex, secondVertex,thirdVertex))
                    return true
            }

            return false
        }}}
    }

    override fun toString():String = "attributes: $attributes, componentCount: $componentCount, size: $size, rawData: ${rawData.joinToString(", ", "[", "]", 128)}".replace(")\n]", ")]").replace("\n", ", ")

    override fun hashCode():Int {
        var hash = size.hashCode();

        for(i in 0..size - 1)
            hash = hash xor rawData[i].hashCode()

        return hash
    }

    override fun equals(other:Any?):Boolean {
        if(other !is Vertices || size != other.size)
            return false

        for(i in 0..size - 1)
            if(rawData[i] != other.rawData[i])
                return false

        return true
    }

    override public fun clone():Any {
        var clone = Vertices(attributes, size)
        clone.size = size

        for(i in 0..size - 1)
            clone.rawData[i] = rawData[i]

        return clone
    }
}
