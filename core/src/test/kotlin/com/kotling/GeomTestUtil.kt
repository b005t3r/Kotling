package com.kotling

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.display.test.DisplayTest
import junit.framework.Assert

fun cmpRect(rectA:Rectangle, rectB:Rectangle):Boolean {
    return MathUtils.isEqual(rectA.x, rectB.x, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.y, rectB.y, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.width, rectB.width, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(rectA.height, rectB.height, DisplayTest.FLOAT_PRECISION);
}

fun cmpVector2(vecA:Vector2, vecB:Vector2):Boolean {
    return MathUtils.isEqual(vecA.x, vecB.x, DisplayTest.FLOAT_PRECISION)
        && MathUtils.isEqual(vecA.y, vecB.y, DisplayTest.FLOAT_PRECISION)
}

@Suppress("DEPRECATION")
fun assertVecEquals(vecA:Vector2, vecB:Vector2) {
    if(!cmpVector2(vecA, vecB))
        Assert.failNotEquals(null, vecA, vecB)
}

@Suppress("DEPRECATION")
fun assertRectEquals(rectA:Rectangle, rectB:Rectangle) {
    if(!cmpRect(rectA, rectB))
        Assert.failNotEquals(null, rectA, rectB)
}
