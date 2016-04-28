package com.kotling.rendering

class IndexData(initialCapacity:Int = 48) : Iterable<Short>, Sequence<Short>, Cloneable {
    var size = 0
        private set

    private var rawData = ShortArray(initialCapacity)

    // does not free memory
    fun clear() { size = 0 }

    fun ensureCapacity(newCapacity:Int) {
        if(rawData.size >= newCapacity)
            return

        rawData = ShortArray(newCapacity, { i -> if(i < rawData.size) rawData[i] else 0 });
    }

    fun copyTo(target:IndexData, targetIndexID:Int = 0, offset:Short = 0, indexID:Int = 0, count:Int = -1) {
        val numIndices = if(count < 0 || indexID + count > size) size - indexID else count
        val newSize = targetIndexID + numIndices

        target.ensureCapacity(newSize)

        for(i in 0..newSize - 1)
            target.rawData[targetIndexID + i] = (rawData[indexID + i] + offset).toShort()
    }

    operator fun get(i:Int):Short = if(i < size) rawData[i] else throw IndexOutOfBoundsException("index $i is outside 0..${size - 1}")
    operator fun set(i:Int, value:Short) = if(i < size) rawData.set(i, value) else throw IndexOutOfBoundsException("index $i is outside 0..${size - 1}")

    /** Appends three indices representing a triangle. Reference the vertices clockwise,
     *  as this defines the front side of the triangle. */
    fun addTriangle(a:Short, b:Short, c:Short) {
        ensureCapacity(size + 3)

        rawData[size]       = a
        rawData[size + 1]   = b
        rawData[size + 2]   = c
        size               += 3
    }

    override fun toString():String = "size: $size, rawData: $rawData"

    override fun hashCode():Int {
        var hash = size.hashCode();

        for(i in 0..size - 1)
            hash = hash xor rawData[i].hashCode()

        return hash
    }

    override fun equals(other:Any?):Boolean {
        if(other !is IndexData || size != other.size)
            return false

        for(i in 0..size - 1)
            if(rawData[i] != other.rawData[i])
                return false

        return true
    }

    override fun clone():Any {
        var clone = IndexData(rawData.size)
        clone.size = size

        for(i in 0..size)
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
