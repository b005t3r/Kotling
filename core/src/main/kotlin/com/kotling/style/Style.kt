package com.kotling.style

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.mesh.MeshDisplay
import com.kotling.rendering.RenderState
import com.kotling.rendering.Renderer
import com.kotling.style.factory.DefaultStyleFactory
import com.kotling.style.factory.StyleFactory

abstract class Style {
    companion object {
        val styleFactory:StyleFactory = DefaultStyleFactory()

        fun createStyle(attrs:VertexAttributes):Style = styleFactory.createStyle(attrs)
    }

    abstract val attributes:VertexAttributes

    open var mesh:MeshDisplay? = null
        set(value) {
            if(value == null) {
                field = null
            }
            else {
                if(value.vertices.attributes == attributes)
                    throw IllegalArgumentException("mesh's vertex attributes (${value.vertices.attributes.toString().replace(")\n]", ")]").replace("\n", ", ")}) don't match style's vertex attributes (${attributes.toString().replace(")\n]", ")]").replace("\n", ", ")})")

                field = value
            }
        }

    var requiresRedraw:Boolean
        get() = mesh?.requiresRedraw ?: throw IllegalStateException("mesh not set")
        internal set(value) { mesh?.requiresRedraw = value }

    val vertices = mesh?.vertices ?: throw IllegalStateException("mesh not set")
    val indices = mesh?.indices ?: throw IllegalStateException("mesh not set")

    open fun set(style:Style) {
        if(! javaClass.isInstance(style))
            throw IllegalArgumentException("$style is not an instance of $javaClass")

        mesh = style.mesh
    }

    abstract fun copy(mesh:MeshDisplay? = this.mesh):Style
    abstract fun canBatchWith(style:Style):Boolean
    abstract fun createRenderer():Renderer
    abstract fun updateRenderer(renderer : Renderer, renderState:RenderState)

}
