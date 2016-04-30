package com.kotling.util

import com.badlogic.gdx.utils.Pools
import com.kotling.util.poolable.PoolableColor
import com.kotling.util.poolable.PoolableMatrix3
import com.kotling.util.poolable.PoolableRectangle
import com.kotling.util.poolable.PoolableVector2

object Pool {
    val Vector2     = Pools.get(PoolableVector2::class.java)
    val Rectangle   = Pools.get(PoolableRectangle::class.java)
    val Matrix3     = Pools.get(PoolableMatrix3::class.java)
    val Color       = Pools.get(PoolableColor::class.java)
}
