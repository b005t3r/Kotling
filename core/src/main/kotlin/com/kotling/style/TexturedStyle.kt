package com.kotling.style

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.MeshDisplay
import com.kotling.display.mesh.TexturesMeshAttribute
import com.kotling.rendering.*

class TexturedStyle : Style() {
    var texture:Texture? = null

    override var mesh:MeshDisplay? = null
        get() = super.mesh
        set(value) {
            super.mesh = value

            if(field == null) {
                texture = null
            }
            else {
                val textureAttr = field?.meshAttributes?.find { it is TexturesMeshAttribute } as? TexturesMeshAttribute ?: throw IllegalStateException("no TexturedMeshAttribute for MeshDisplay: $field (attributes: ${field?.meshAttributes}")

                texture = textureAttr.textures[0]
            }
        }

    @VertexFormat("position:float2", "color:byte4", "texCoords:float2")
    override val attributes:VertexAttributes by VertexAttributesCache

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
