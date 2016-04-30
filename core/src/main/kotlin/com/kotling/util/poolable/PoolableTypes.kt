package com.kotling.util.poolable

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class PoolableVector2 : Vector2(), Pool.Poolable {
    override fun reset() { set(0f, 0f) }
}

class PoolableMatrix3 : Matrix3(), Pool.Poolable {
    override fun reset() { idt() }
}

class PoolableRectangle : Rectangle(), Pool.Poolable {
    override fun reset() { set(0f, 0f, 0f, 0f) }
}

class PoolableColor : Color(), Pool.Poolable {
    override fun reset() { this.set(0.0f, 0.0f, 0.0f, 0.0f) }
}

inline fun <T : Pool.Poolable, R> T.use(pool:Pool<T>, block : (T) -> R): R {
    var freed = false
    try {
        return block(this)
    } catch (e: Exception) {
        freed = true
        pool.free(this)
        throw e
    } finally {
        if (! freed)
            pool.free(this)
    }
}

inline fun <T : Pool.Poolable, R> Pool<T>.use(block : (T) -> R): R {
    return obtain().use(this, block)
}
