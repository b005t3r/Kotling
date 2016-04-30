package com.kotling.util

import com.badlogic.gdx.utils.NumberUtils

fun Float.fromFloatBits():Int {
    var c = NumberUtils.floatToIntBits(this) and 0xfeffffff.toInt()

    return if(c and 0xfe000000.toInt() == 0) c and 0x00ffffff.toInt() else c or 0x01000000
}
