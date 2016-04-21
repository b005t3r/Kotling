package com.kotling.display

import com.badlogic.gdx.math.Rectangle
import com.kotling.rendering.Painter

class Stage : Container() {
    override fun getBounds(targetSpace:Display?, result:Rectangle?):Rectangle {
        throw UnsupportedOperationException()
    }

    override fun render(painter:Painter) {
        throw UnsupportedOperationException()
    }
}