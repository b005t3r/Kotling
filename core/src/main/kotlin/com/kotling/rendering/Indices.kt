package com.kotling.rendering

import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.math.EarClippingTriangulator

class Indices(initialCapacity:Int = MIN_CAPACITY) : Iterable<Short>, Sequence<Short>, Cloneable {
    private object Triangulator : EarClippingTriangulator() {
    //private object Triangulator : DelaunayTriangulator() {
        private var tempBuffer:FloatArray = FloatArray(MIN_CAPACITY * 2) { i -> Float.NaN }

        fun computeTriangles(vertices:Vertices, vertexID:Int = 0, count:Int = -1):com.badlogic.gdx.utils.ShortArray {
            if(vertexID !in 0..vertices.size - 1)
                throw IllegalArgumentException("vertexID $vertexID outside of vertices bounds 0..${vertices.size - 1}")

            val numVertices = if(count < 0 || vertexID + count > vertices.size) vertices.size - vertexID else count

            ensureCapacity(numVertices * 2)

            val dst = tempBuffer
            var dstOffset = 0

            vertices.forEach(vertexID, numVertices) { vertexID, attrID, src, srcOffset, count ->
                if(vertices.attributes[attrID].usage == VertexAttributes.Usage.Position) {
                    dst[dstOffset++] = src[srcOffset]
                    dst[dstOffset++] = src[srcOffset + 1]
                }
            }

            return computeTriangles(dst, 0, dstOffset)
        }

        private fun ensureCapacity(newCapacity:Int) {
            if(tempBuffer.size >= newCapacity)
                return

            val newSize = ((newCapacity / MIN_CAPACITY) + 1) * MIN_CAPACITY
            tempBuffer = FloatArray(newSize) { i -> if(i < tempBuffer.size) tempBuffer[i] else Float.NaN }
        }
    }

    companion object {
        const val MIN_CAPACITY:Int = 16

        fun triangulate(vertices:Vertices, vertexID:Int = 0, count:Int = -1):Indices {
            val triangles = Triangulator.computeTriangles(vertices, vertexID, count)
            val indices = Indices(triangles.size)


            for(i in 0..triangles.size - 1)
                indices.add(triangles[i])

            return indices
        }
    }

    var size = 0
        private set

    var rawData = ShortArray(Math.max(MIN_CAPACITY, initialCapacity)) { i -> -1 }
        private set

    fun clear(trim:Boolean = false) {
        size = 0

        if(trim)
            trim()
    }

    fun trim() {
        if(rawData.size > MIN_CAPACITY)
            rawData = ShortArray(Math.max(size, MIN_CAPACITY), { i -> rawData[i] })
    }

    fun ensureCapacity(newCapacity:Int):Indices {
        if(rawData.size >= newCapacity)
            return this

        val newSize = ((newCapacity / MIN_CAPACITY) + 1) * MIN_CAPACITY
        rawData = ShortArray(newSize) { i -> if(i < rawData.size) rawData[i] else -1 }

        return this
    }

    fun copyTo(target:Indices, targetIndexID:Int = 0, offset:Short = 0, indexID:Int = 0, count:Int = -1) {
        if(targetIndexID !in 0..target.size)
            throw IndexOutOfBoundsException("targetIndexID $targetIndexID is outside 0..${target.size - 1}")

        if(size == 0)
            return

        if(indexID !in 0..size - 1)
            throw IndexOutOfBoundsException("indexID $indexID is outside 0..${size - 1}")

        val numIndices = if(count < 0 || indexID + count > size) size - indexID else count
        val newSize = targetIndexID + numIndices

        target.ensureCapacity(newSize)

        for(i in 0..newSize - 1)
            target.rawData[targetIndexID + i] = (rawData[indexID + i] + offset).toShort()

        target.size = newSize
    }

    operator fun get(i:Int):Short = if(i in 0..size - 1) rawData[i] else throw IndexOutOfBoundsException("index $i is outside 0..${size - 1}")
    operator fun set(i:Int, value:Short) = if(i in 0..size - 1) rawData.set(i, value) else throw IndexOutOfBoundsException("index $i is outside 0..${size - 1}")

    fun add(index:Short):Indices {
        ensureCapacity(size + 1)

        rawData[size]   = index
        size           += 1

        return this
    }

    /** Appends three indices representing a triangle. Reference the vertices clockwise,
     *  as this defines the front side of the triangle. */
    fun add(a:Short, b:Short, c:Short):Indices {
        ensureCapacity(size + 3)

        rawData[size]       = a
        rawData[size + 1]   = b
        rawData[size + 2]   = c
        size               += 3

        return this
    }

    fun offset(offset:Short, indexID:Int = 0, count:Int = -1):Indices {
        if(indexID !in 0..size - 1)
            throw IndexOutOfBoundsException("index $indexID is outside 0..${size - 1}")

        val numIndices = if(count < 0 || indexID + count > size) size - indexID else count

        for(i in indexID..indexID + numIndices - 1)
            this[i] = (this[i] + offset).toShort()

        return this
    }

    override fun toString():String = "size: $size, rawData: ${rawData.joinToString(", ", "[", "]", 128)}"

    override fun hashCode():Int {
        var hash = size.hashCode();

        for(i in 0..size - 1)
            hash = hash xor rawData[i].hashCode()

        return hash
    }

    override fun equals(other:Any?):Boolean {
        if(other !is Indices || size != other.size)
            return false

        for(i in 0..size - 1)
            if(rawData[i] != other.rawData[i])
                return false

        return true
    }

    override public fun clone():Any {
        var clone = Indices(rawData.size)
        clone.size = size

        for(i in 0..size - 1)
            clone.rawData[i] = rawData[i]

        return clone
    }

    override fun iterator():Iterator<Short> = IndexIterator()

    inner private class IndexIterator : ShortIterator() {
        var index = 0

        override fun nextShort():Short = if(index < size) rawData[index++] else throw IndexOutOfBoundsException("iterating beyond collections end")
        override fun hasNext():Boolean = index < size
    }
}
