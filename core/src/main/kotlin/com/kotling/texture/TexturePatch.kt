package com.kotling.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.*

class TexturePatch(
    val texture:Texture,
    val region:Rectangle,
    val frame:Rectangle? = null,
    val polygon:Polygon? = null,
    val transform:Transform = TexturePatch.Transform.NONE,
    val scale:Float = 1f) {

    enum class Transform(val matrix:Matrix3) {
        NONE(Matrix3()),
        CLOCKWISE(Matrix3().translate(1f, 0f).rotateRad(MathUtils.PI * 0.5f)),
        COUNTERCLOCKWISE(Matrix3().translate(0f, 1f).rotateRad(MathUtils.PI * -0.5f)),
        UPSIDE_DOWN(Matrix3().translate(1f, 1f).rotateRad(MathUtils.PI)),
        HORIZONTAL_FLIP(Matrix3().translate(1f, 0f).scale(-1f, 1f)),
        VERTICAL_FLIP(Matrix3().translate(0f, 1f).scale(1f, -1f)),
        CLOCKWISE_FLIP(Matrix3().scale(-1f, 1f).rotateRad(MathUtils.PI * 0.5f)),
        COUNTERCLOCKWISE_FLIP(Matrix3().translate(1f, 1f).scale(-1f, 1f).rotateRad(MathUtils.PI * -0.5f)),
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
            .mul(transform.matrix)

        rootTransformationMatrix.set(transformationMatrix)

        minUV.mul(transformationMatrix)
        maxUV.mul(transformationMatrix)
    }

    constructor(patch:TexturePatch, region:Rectangle, frame:Rectangle? = null, polygon:Polygon? = null, transform:Transform = Transform.NONE, scaleMultiplier:Float = 1f)
    : this(patch.texture, region, frame, polygon, transform, patch.scale * scaleMultiplier) {
        parent = patch

        transformationMatrix
            .idt()
            .translate(region.x / patch.region.width, region.y / patch.region.height)
            .scale(region.width / patch.region.width, region.height / patch.region.height)
            .mul(transform.matrix)

        rootTransformationMatrix.set(transformationMatrix)

        var p = patch
        while(true) {
            rootTransformationMatrix.mul(p.transformationMatrix)
            p = p.parent ?: break
        }
    }
}
