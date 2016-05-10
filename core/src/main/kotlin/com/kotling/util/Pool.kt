package com.kotling.util

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import com.kotling.util.poolable.PoolableColor
import com.kotling.util.poolable.PoolableMatrix3
import com.kotling.util.poolable.PoolableRectangle
import com.kotling.util.poolable.PoolableVector2

object Pool {
    val Vector2     = get<PoolableVector2>()
    val Rectangle   = get<PoolableRectangle>()
    val Matrix3     = get<PoolableMatrix3>()
    val Color       = get<PoolableColor>()

    inline fun <reified T : Pool.Poolable> get():Pool<T> = Pools.get(T::class.java)
}
