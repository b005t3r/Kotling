package com.kotling.rendering

import com.badlogic.gdx.utils.Pool

data class BatchToken(var batchID:Int = 0, var vertexID:Int = 0, var indexID:Int = 0) : Pool.Poolable {
    fun set(batchID:Int = 0, vertexID:Int = 0, indexID:Int = 0):BatchToken {
        this.batchID = batchID
        this.vertexID = vertexID
        this.indexID = indexID

        return this
    }

    fun set(token:BatchToken):BatchToken = set(token.batchID, token.vertexID, token.indexID)

    override fun reset() { set() }
}
