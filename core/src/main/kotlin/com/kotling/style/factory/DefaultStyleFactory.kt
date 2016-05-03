package com.kotling.style.factory

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.rendering.VertexAttributesCache
import com.kotling.rendering.VertexFormat
import com.kotling.style.ColoredStyle
import com.kotling.style.Style
import com.kotling.style.TexturedStyle

open class DefaultStyleFactory : StyleFactory {
    @VertexFormat("position:float2", "color:byte4")
    open val coloredFormat:VertexAttributes by VertexAttributesCache

    @VertexFormat("position:float2", "color:byte4", "texCoords:float2")
    open val texturedFormat:VertexAttributes by VertexAttributesCache

    override fun createStyle(attributes:VertexAttributes):Style = when(attributes) {
        coloredFormat -> ColoredStyle()
        texturedFormat -> TexturedStyle()
        else -> throw IllegalArgumentException("unhandled attributes: $attributes")
    }
}
