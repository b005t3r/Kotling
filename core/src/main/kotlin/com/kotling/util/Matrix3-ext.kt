package com.kotling.util

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4

var Matrix3.a:Float get() { return `val`[Matrix3.M00]} set(value) { `val`[Matrix3.M00] = value }
var Matrix3.b:Float get() { return `val`[Matrix3.M10]} set(value) { `val`[Matrix3.M10] = value }
var Matrix3.c:Float get() { return `val`[Matrix3.M01]} set(value) { `val`[Matrix3.M01] = value }
var Matrix3.d:Float get() { return `val`[Matrix3.M11]} set(value) { `val`[Matrix3.M11] = value }
var Matrix3.tx:Float get() { return `val`[Matrix3.M02]} set(value) { `val`[Matrix3.M02] = value }
var Matrix3.ty:Float get() { return `val`[Matrix3.M12]} set(value) { `val`[Matrix3.M12] = value }

fun Matrix3.setLastRow():Matrix3 {
    `val`[Matrix3.M20] = 0f
    `val`[Matrix3.M21] = 0f
    `val`[Matrix3.M22] = 1f

    return this
}

fun Matrix3.setTo(a:Float, b:Float, c:Float, d:Float, tx:Float, ty:Float):Matrix3 {
    this.a = a
    this.b = b
    this.c = c
    this.d = d
    this.tx = tx
    this.ty = ty

    return setLastRow()
}

fun Matrix3.skew(skewX:Float, skewY:Float):Matrix3 {
    var sinX = MathUtils.sin(skewX)
    var cosX = MathUtils.cos(skewX)
    var sinY = MathUtils.sin(skewY)
    var cosY = MathUtils.cos(skewY)

    setTo(
        a  * cosY - b  * sinX,
        a  * sinY + b  * cosX,
        c  * cosY - d  * sinX,
        c  * sinY + d  * cosX,
        tx * cosY - ty * sinX,
        tx * sinY + ty * cosX
    )

    return this
}

fun Matrix3.from3D(m:Matrix4):Matrix3 {
    `val`[Matrix3.M00] = m.`val`[Matrix4.M00]
    `val`[Matrix3.M01] = m.`val`[Matrix4.M01]
    `val`[Matrix3.M10] = m.`val`[Matrix4.M10]
    `val`[Matrix3.M11] = m.`val`[Matrix4.M11]
    `val`[Matrix3.M02] = m.`val`[Matrix4.M03]
    `val`[Matrix3.M12] = m.`val`[Matrix4.M13]

    return setLastRow()
}
