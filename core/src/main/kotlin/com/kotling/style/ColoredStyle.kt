package com.kotling.style

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.MeshDisplay
import com.kotling.rendering.*

class ColoredStyle : Style() {
    @VertexFormat("position:float2", "color:byte4")
    override val attributes:VertexAttributes by VertexAttributesCache

    override fun copy(mesh:MeshDisplay?):Style {
        val style = ColoredStyle()
        style.mesh = mesh

        return style
    }

    override fun canBatchWith(style:Style):Boolean {
        if(style !is ColoredStyle)
            return false

        return true
    }

    override fun createRenderer():Renderer = ColoredRenderer()
    override fun updateRenderer(renderer:Renderer, renderState:RenderState) {
        if(renderer !is ColoredRenderer)
            throw IllegalArgumentException("unsupported renderer type: $renderer")

        renderer.globalColor = renderState.color
    }
}
