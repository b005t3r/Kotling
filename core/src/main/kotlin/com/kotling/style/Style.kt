package com.kotling.style

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.MeshDisplay
import com.kotling.rendering.RenderState
import com.kotling.rendering.Renderer

abstract class Style : Cloneable {
    companion object {
        fun createStyleForVertexAttributes(attrs:VertexAttributes):Style {
            throw UnsupportedOperationException("not yet implemented");
        }
    }

    abstract val attributes:VertexAttributes

    open var mesh:MeshDisplay? = null

    val vertices = mesh?.vertices ?: throw IllegalStateException("mesh not set")
    val indices = mesh?.vertices ?: throw IllegalStateException("mesh not set")

    open fun copyFrom(style:Style) {
        mesh = style.mesh
    }

    abstract fun canBatchWith(style:Style):Boolean
    abstract fun createRenderer():Renderer
    abstract fun updateRenderer(renderer : Renderer, renderState:RenderState)

    protected fun setRequiresRedraw() { mesh?.requiresRedraw = true }
}
