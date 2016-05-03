package com.kotling.style.factory

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.style.Style

interface StyleFactory {
    fun createStyle(attributes:VertexAttributes):Style
}
