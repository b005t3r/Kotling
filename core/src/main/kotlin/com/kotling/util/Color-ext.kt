package com.kotling.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.NumberUtils

fun Color.set(abgr:Float):Color {
    var abgri = NumberUtils.floatToIntBits(abgr)

    val ai = abgri and 0xfe000000.toInt() ushr 24

    a = (if(ai != 0) ai or 0x01 else ai) / 255.0f
    b = (abgri and 0x00ff0000 ushr 16) / 255.0f
    g = (abgri and 0x0000ff00 ushr 8) / 255.0f
    r = (abgri and 0x000000ff) / 255.0f

    return this
}
