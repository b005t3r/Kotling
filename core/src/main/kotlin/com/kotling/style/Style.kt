package com.kotling.style

import com.badlogic.gdx.graphics.VertexAttributes
import com.kotling.display.MeshDisplay
import com.kotling.rendering.RenderState
import com.kotling.rendering.Renderer
import com.kotling.style.factory.DefaultStyleFactory
import com.kotling.style.factory.StyleFactory

abstract class Style : Cloneable {
    companion object {
        val styleFactory:StyleFactory = DefaultStyleFactory()

        fun createStyle(attrs:VertexAttributes):Style = styleFactory.createStyle(attrs)
    }

    abstract val attributes:VertexAttributes

    open var mesh:MeshDisplay? = null

    val vertices = mesh?.vertices ?: throw IllegalStateException("mesh not set")
    val indices = mesh?.vertices ?: throw IllegalStateException("mesh not set")

    open fun set(style:Style) {
        if(! javaClass.isInstance(style))
            throw IllegalArgumentException("$style is not an instance of $javaClass")

        mesh = style.mesh
    }

    abstract fun canBatchWith(style:Style):Boolean
    abstract fun createRenderer():Renderer
    abstract fun updateRenderer(renderer : Renderer, renderState:RenderState)

    protected fun setRequiresRedraw() { mesh?.requiresRedraw = true }
}
