package com.kotling.display

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pools
import com.kotling.poolable.PoolableMatrix3
import com.kotling.poolable.PoolableRectangle
import com.kotling.poolable.PoolableVector2
import com.kotling.poolable.use
import com.kotling.rendering.Painter
import com.kotling.util.*

abstract class Display : Disposable {
    object Pool {
        val Vector2     = Pools.get(PoolableVector2::class.java)
        val Rectangle   = Pools.get(PoolableRectangle::class.java)
        val Matrix3     = Pools.get(PoolableMatrix3::class.java)
    }

    companion object {
        private val ancestors = mutableListOf<Display>()

        fun findCommonParent(object1:Display, object2:Display):Display {
            var current = object1

            while(true) {
                ancestors.add(current)

                current = current.parent ?: break
            }

            current = object2

            while(true) {
                var common = ancestors.find { it == current }

                if(common != null) {
                    ancestors.clear()
                    return common
                }

                current = current.parent ?: break
            }

            ancestors.clear()
            throw IllegalArgumentException("no common parent for $object1 and $object2")
        }
    }

    var parent:Container? = null
        internal set(value) {
            var ancestor = value

            while(ancestor != this && ancestor != null)
                ancestor = ancestor.parent

            if(ancestor == this)
                throw IllegalArgumentException("an object cannot be added as a child to itself or one of its children (or children's children, etc.)")

            field = value
        }

    val base:Display get() = parent?.base ?: this
    val stage:Stage? get() = base as? Stage

    var name = ""

    var x = 0f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var y = 0f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var width:Float
        get() = bounds.width
        set(value) {
            var actualWidth:Float

            if (scaleX == 0f) {
                scaleX = 1f
                actualWidth = width
            }
            else {
                actualWidth = width / scaleX
            }

            if (actualWidth != 0f)
                scaleX = value / actualWidth;
        }

    var height:Float
        get() = bounds.height
        set(value) {
            var actualHeight:Float

            if (scaleY == 0f) {
                scaleY = 1f
                actualHeight = height
            }
            else {
                actualHeight = height / scaleY
            }

            if (actualHeight != 0f)
                scaleY = value / actualHeight;
        }

    var pivotX = 0f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var pivotY = 0f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var pivotAlignmentX:Float
        get() = (pivotX - internalBounds.x) / internalBounds.width
        set(value) { pivotX = internalBounds.x + internalBounds.width * value }

    var pivotAlignmentY:Float
        get() = (pivotY - internalBounds.y) / internalBounds.height
        set(value) { pivotY = internalBounds.y + internalBounds.height * value }

    var scaleX = 1f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var scaleY = 1f
        set(value) {
            if(value == field) return
            orientationChanged = true
            field = value
        }

    var skewX = 0f
        set(value) {
            val normalized = if(value > 0) value % MathUtils.PI2 else if(value < 0) ((value % MathUtils.PI2) + MathUtils.PI2) % MathUtils.PI2 else 0f

            if(value == normalized) return
            orientationChanged = true
            field = normalized
        }

    var skewY = 0f
        set(value) {
            val normalized = if(value > 0) value % MathUtils.PI2 else if(value < 0) ((value % MathUtils.PI2) + MathUtils.PI2) % MathUtils.PI2 else 0f

            if(value == normalized) return
            orientationChanged = true
            field = normalized
        }

    val bounds = Rectangle()
        get() = getBounds(parent, field)

    val internalBounds = Rectangle()
        get() = getBounds(this, field)

    var alpha = 1f
        set(value) {
            if(value == field) return
            requiresRedraw = true
            field = MathUtils.clamp(value, 0f, 1f)
        }

    var rotation = 0f
        set(value) {
            val normalized = if(value > 0) value % MathUtils.PI2 else if(value < 0) (value % MathUtils.PI2) + MathUtils.PI2 else 0f

            if(field == normalized) return
            orientationChanged = true
            field = normalized
        }

    var visible = true
        set(value) {
            if(value == field) return
            requiresRedraw = true
            field = value
        }

    var touchable = true

    var blendMode = BlendMode.AUTO
        set(value) {
            if(value == field) return
            requiresRedraw = true
            field = value
        }

    var transformationMatrix = Matrix3()
        get() {
            if(! orientationChanged)
                return field

                if(skewX == 0f && skewY == 0f) {
                    if(rotation == 0f) {
                        return field.setTo(scaleX, 0f, 0f, scaleY, x - pivotX * scaleX, y - pivotY * scaleY)
                    }
                    else {
                        val cos = MathUtils.cos(rotation)
                        val sin = MathUtils.sin(rotation)
                        val a   = scaleX * cos
                        val b   = scaleX * sin
                        val c   = scaleY * -sin
                        val d   = scaleY * cos
                        val tx  = x - pivotX * a - pivotY * c
                        val ty  = y - pivotX * b - pivotY * d

                        return field.setTo(a, b, c, d, tx, ty)
                    }
                }
                else {
                    throw UnsupportedOperationException("handling skewX and skewY has bugs")

                    field.idt().scale(scaleX, scaleY).skew(skewX, skewY).rotateRad(rotation).translate(x, y);

                    if (pivotX != 0f || pivotY != 0f) {
                        // prepend pivot transformation
                        field.tx = x - field.a * pivotX - field.c * pivotY;
                        field.ty = y - field.b * pivotX - field.d * pivotY;
                    }

                    return field
                }
        }
        set(value) {
            requiresRedraw = true
            orientationChanged = false

            field.set(value)
            pivotX = 0f
            pivotY = 0f

            x = value.tx
            y = value.ty

            skewX = MathUtils.atan2(-value.c, value.d)
            skewY = MathUtils.atan2( value.b, value.a)

            if(MathUtils.isEqual(skewX, skewY)) {
                rotation = skewX
                skewX = 0f
                skewY = 0f
            }
            else {
                rotation = 0f
            }
        }

    var requiresRedraw = true
        internal set

    var orientationChanged = true
        internal set

    override fun dispose() {
    }

    open fun removeFromParent(dispose:Boolean = false) {
        parent?.children?.remove(this, dispose) ?: dispose()
    }

    open fun hitTest(localPoint:Vector2):Display? {
        if(! visible || ! touchable)
            return null

        //TODO: if(mask != null && ! hitTestMask(localPoint)) return null

        return if(internalBounds.contains(localPoint)) this else null
    }

    fun getTransformationMatrix(targetSpace:Display?, result:Matrix3? = null):Matrix3 {
        val out = result?.idt() ?: Matrix3()

        if(targetSpace == this) {
            return out
        }
        else if(targetSpace == parent) {
            return out.set(transformationMatrix)
        }
        else if(targetSpace == null || targetSpace == base) {
            var current:Display = this

            while(current != targetSpace) {
                out.mulLeft(current.transformationMatrix)
                current = current.parent ?: break
            }

            return out
        }
        else if(targetSpace.parent == this) {
            return targetSpace.getTransformationMatrix(this, out).inv()
        }

        var commonParent = findCommonParent(this, targetSpace)

        var current = this
        while(current != commonParent) {
            out.mulLeft(current.transformationMatrix)
            current = current.parent ?: throw IllegalStateException("common parent found, but somehow iterated past it - impossible")
        }

        if(commonParent == targetSpace)
            return out

        Pool.Matrix3.use {
            current = targetSpace
            while(current != commonParent) {
                it.mulLeft(current.transformationMatrix)
                current = current.parent ?: throw IllegalStateException("common parent found, but somehow iterated past it - impossible")
            }

            return out.mul(it.inv())
        }
    }

    fun localToGlobal(localPoint:Vector2, result:Vector2? = null):Vector2 {
        val out = result ?: Vector2()

        //TODO: if(is3D)

        Pool.Matrix3.use {
            return out.set(localPoint).mul(getTransformationMatrix(base, it))
        }
    }

    fun globalToLocal(globalPoint:Vector2, result:Vector2? = null):Vector2 {
        val out = result ?: Vector2()

        //TODO: if(is3D)

        Pool.Matrix3.use {
            return out.set(globalPoint).mul(getTransformationMatrix(base, it).inv())
        }
    }

    abstract fun getBounds(targetSpace:Display?, result:Rectangle? = null):Rectangle
    abstract fun render(painter:Painter)
}