package com.kotling.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.*

class TexturePatch(
        val texture:Texture,
        val region:Rectangle,
        val frame:Rectangle? = null,
        val polygon:Polygon? = null,
        val rotation:Rotation = TexturePatch.Rotation.NONE,
        val scale:Float = 1f) {

    enum class Rotation(val matrix:Matrix3) {
        NONE(Matrix3()),
        CLOCKWISE(Matrix3().translate(1f, 0f).rotateRad(MathUtils.PI * 0.5f)),
        COUNTERCLOCKWISE(Matrix3().translate(0f, 1f).rotateRad(MathUtils.PI * -0.5f)),
        UPSIDE_DOWN(Matrix3().translate(1f, 1f).rotateRad(MathUtils.PI))
    }

    var parent:TexturePatch? = null
        private set

    val transformationMatrix:Matrix3 = Matrix3()
    val rootTransformationMatrix:Matrix3 = Matrix3()

    val minUV = Vector2(0f, 0f)
    val maxUV = Vector2(1f, 1f)

    init {
        transformationMatrix
            .translate(region.x / texture.width, region.y / texture.height)
            .scale(region.width / texture.width, region.height / texture.height)
            .mul(rotation.matrix)

        rootTransformationMatrix.set(transformationMatrix)

        minUV.mul(transformationMatrix)
        maxUV.mul(transformationMatrix)
    }

    constructor(patch:TexturePatch, region:Rectangle, frame:Rectangle? = null, polygon:Polygon? = null, rotation:Rotation = Rotation.NONE, scaleMultiplier:Float = 1f)
    : this(patch.texture, region, frame, polygon, rotation, patch.scale * scaleMultiplier) {
        parent = patch

        transformationMatrix
            .idt()
            .translate(region.x / patch.region.width, region.y / patch.region.height)
            .scale(region.width / patch.region.width, region.height / patch.region.height)
            .mul(rotation.matrix)

        rootTransformationMatrix.set(transformationMatrix)

        var p = patch
        while(true) {
            rootTransformationMatrix.mul(p.transformationMatrix)
            p = p.parent ?: break
        }
    }
}
