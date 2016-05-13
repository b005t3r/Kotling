package com.kotling.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Disposable
import com.kotling.util.Pool
import com.kotling.util.poolable.use

data class TexturePatch(
    val texture:Texture,
    val region:Rectangle,
    val frame:Rectangle = Rectangle().setPosition(0f, 0f).setSize(region.width, region.height),
    val vertices:FloatArray = floatArrayOf(0f, 0f, region.width, 0f, 0f, region.height, region.width, region.height),
    val indices:ShortArray = shortArrayOf(0, 1, 2, 1, 3, 2),
    val transform:Transform = TexturePatch.Transform.NONE,
    val scale:Float = 1f) : Disposable {

    enum class Transform(val matrix:Matrix3, val rotated:Boolean = false) {
        NONE(Matrix3()),
        CLOCKWISE(Matrix3().translate(1f, 0f).rotateRad(MathUtils.PI * 0.5f), true),
        COUNTERCLOCKWISE(Matrix3().translate(0f, 1f).rotateRad(MathUtils.PI * -0.5f), true),
        UPSIDE_DOWN(Matrix3().translate(1f, 1f).rotateRad(MathUtils.PI)),
        HORIZONTAL_FLIP(Matrix3().translate(1f, 0f).scale(-1f, 1f)),
        VERTICAL_FLIP(Matrix3().translate(0f, 1f).scale(1f, -1f)),
        CLOCKWISE_FLIP(Matrix3().scale(-1f, 1f).rotateRad(MathUtils.PI * 0.5f), true),
        COUNTERCLOCKWISE_FLIP(Matrix3().translate(1f, 1f).scale(-1f, 1f).rotateRad(MathUtils.PI * -0.5f), true),
    }

    var parent:TexturePatch? = null
        private set

    val uvMatrix = Matrix3()
    val uvInvMatrix = Matrix3()
    val uvRootMatrix = Matrix3()

    val minUV = Vector2(0f, 0f)
    val maxUV = Vector2(1f, 1f)

    init {
        uvMatrix
            .translate(region.x / texture.width, region.y / texture.height)
            .scale(region.width / texture.width, region.height / texture.height)
            .mul(transform.matrix)

        uvInvMatrix.set(uvMatrix).inv()

        uvRootMatrix.set(uvMatrix)

        minUV.mul(uvMatrix)
        maxUV.mul(uvMatrix)
    }

    constructor(patch:TexturePatch, region:Rectangle, frame:Rectangle = Rectangle().setPosition(0f, 0f).setSize(region.width, region.height), vertices:FloatArray = floatArrayOf(0f, 0f, region.width, 0f, 0f, region.height, region.width, region.height), indices:ShortArray = shortArrayOf(0, 1, 2, 1, 3, 2), transform:Transform = Transform.NONE, scaleMultiplier:Float = 1f)
    : this(patch.texture, region, frame, vertices, indices, transform, patch.scale * scaleMultiplier) {
        parent = patch

        uvMatrix
            .idt()
            .translate(region.x / patch.region.width, region.y / patch.region.height)
            .scale(region.width / patch.region.width, region.height / patch.region.height)
            .mul(transform.matrix)

        uvRootMatrix.set(uvMatrix)

        var p = patch
        while(true) {
            uvRootMatrix.mul(p.uvMatrix)
            p = p.parent ?: break
        }
    }

    override fun dispose() { parent?.dispose() ?: texture.dispose() }
}

fun Vector2.transform(patch:TexturePatch):Vector2 {
    add(patch.region.x, patch.region.y)
    if(patch.parent == null) {
        x /= patch.texture.width
        y /= patch.texture.height
    }
    else {
        x /= patch.parent!!.region.width
        y /= patch.parent!!.region.height
    }

    mul(patch.uvInvMatrix)

    if(patch.transform.rotated) {
        x *= patch.region.height
        y *= patch.region.width
    }
    else {
        x *= patch.region.width
        y *= patch.region.height
    }

    return this
}

fun Rectangle.transform(patch:TexturePatch):Rectangle {
    Pool.Vector2.use { p ->
        p.set(x, y).transform(patch)
        val tmpX = p.x
        val tmpY = p.y
        p.set(x + width, y + height).transform(patch)

        return set(tmpX, tmpY, 0f, 0f).merge(p.x, p.y)
    }
}