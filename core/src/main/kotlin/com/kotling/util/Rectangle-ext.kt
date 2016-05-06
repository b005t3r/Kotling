package com.kotling.util

import com.badlogic.gdx.math.GeometryUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.kotling.util.poolable.use

fun Rectangle.mul(matrix:Matrix3):Rectangle {
    Pool.Vector2.use { min ->
    Pool.Vector2.use { max ->
        getPosition(min)
        getSize(max).add(min)

        min.mul(matrix)
        max.mul(matrix)

        x = Math.min(min.x, max.x)
        y = Math.min(min.y, max.y)
        width = Math.max(min.x, max.x) - x
        height = Math.max(min.y, max.y) - y
    }}

    return this
}
