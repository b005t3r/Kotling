package com.kotling.style

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.MeshDisplay
import com.kotling.display.mesh.textures
import com.kotling.rendering.*

class TexturedStyle : Style() {
    var texture:Texture? = null
        protected set

    override var mesh:MeshDisplay? = null
        set(value) {
            super.mesh = value

            texture = value?.textures?.get(0)
        }

    @VertexFormat("position:float2", "color:byte4", "texCoords:float2")
    override val attributes:VertexAttributes by VertexAttributesCache

    override fun copy(mesh:MeshDisplay?):Style {
        val style = TexturedStyle()
        style.mesh = mesh
        style.texture = texture

        return style
    }

    override fun canBatchWith(style:Style):Boolean {
        if(style !is TexturedStyle)
            return false

        if(texture != style.texture)
            return false

        return true
    }

    override fun createRenderer():Renderer = TexturedRenderer()
    override fun updateRenderer(renderer:Renderer, renderState:RenderState) {
        if(renderer !is TexturedRenderer)
            throw IllegalArgumentException("unhandled renderer type: $renderer")

        renderer.globalColor = renderState.color
        renderer.texture = texture ?: throw IllegalStateException("texture not set")
    }
}
