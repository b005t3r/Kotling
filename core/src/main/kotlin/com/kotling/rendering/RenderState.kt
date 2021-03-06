package com.kotling.rendering

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import com.kotling.display.BlendMode
import kotlin.reflect.primaryConstructor

class RenderState : Pool.Poolable {
    var color = Color.WHITE.cpy()
        set(value) { field.set(value) }

    var blendMode = BlendMode.NORMAL
        set(value) {
            if(value == field)
                return

            onDrawRequired!!()
            field = value
        }
    var renderTarget:FrameBuffer? = null
        set(value) {
            if(value == field)
                return

            onDrawRequired!!()
            field = value
        }
    var clipRect:Rectangle? = null
        set(value) {
            if(value == field)
                return

            onDrawRequired!!()

            if(value != null)
                field = field?.set(value) ?: Rectangle(value)
            else
                field = null
        }

    private var onDrawRequired:(()->Unit)? = null

    var modelViewMatrix = Matrix3()
    var camera:Camera = OrthographicCamera()

    override fun reset() {
        color               = Color.WHITE
        blendMode           = BlendMode.NORMAL
        renderTarget        = null;
        clipRect            = null;

        modelViewMatrix.idt()

        camera.position.set(0f, 0f, 0f)
        camera.direction.set(0f, 0f, -1f)
        camera.up.set(0f, 1f, 0f)

        camera.projection.idt()
        camera.view.idt()

        camera.near = 1f
        camera.far = 100f
        camera.viewportWidth = 0f
        camera.viewportHeight = 0f

        if(camera is OrthographicCamera)
            (camera as OrthographicCamera).zoom = 1f
        else if(camera is PerspectiveCamera)
            (camera as PerspectiveCamera).fieldOfView = 67f

        camera.update()
    }

    fun set(renderState:RenderState):RenderState {
        reset()

        if(onDrawRequired != null) {
            val clipRectChanges = clipRect != null && renderState.clipRect == null
                || clipRect == null && renderState.clipRect != null
                || clipRect != null && renderState.clipRect != null && clipRect != renderState.clipRect

            if(blendMode != renderState.blendMode || renderTarget != renderState.renderTarget || clipRectChanges)
                onDrawRequired!!()
        }

        color               = renderState.color
        blendMode           = renderState.blendMode
        renderTarget        = renderState.renderTarget;
        clipRect            = if(renderState.clipRect == null) null else Rectangle(renderState.clipRect)

        modelViewMatrix.set(renderState.modelViewMatrix)

        if(camera.javaClass.kotlin != renderState.camera.javaClass.kotlin)
            camera = renderState.camera.javaClass.kotlin.primaryConstructor!!.call()

        camera.position.set(renderState.camera.position)
        camera.direction.set(renderState.camera.direction)
        camera.up.set(renderState.camera.up)

        camera.projection.set(renderState.camera.projection)
        camera.view.set(renderState.camera.view)

        camera.near = renderState.camera.near
        camera.far = renderState.camera.far
        camera.viewportWidth = renderState.camera.viewportWidth
        camera.viewportHeight = renderState.camera.viewportHeight

        if(renderState.camera is OrthographicCamera)
            (camera as OrthographicCamera).zoom = (renderState.camera as OrthographicCamera).zoom
        else if(renderState.camera is PerspectiveCamera)
            (camera as PerspectiveCamera).fieldOfView = (renderState.camera as PerspectiveCamera).fieldOfView

        camera.update()

        return this
    }
}