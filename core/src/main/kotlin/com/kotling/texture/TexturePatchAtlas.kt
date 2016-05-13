package com.kotling.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import com.kotling.util.Pool
import com.kotling.util.poolable.PoolableStringList
import com.kotling.util.poolable.use

class TexturePatchAtlas(val texture:Texture, val scale:Float = 1f) : Disposable {
    constructor(patch:TexturePatch) : this(patch.texture, patch.scale) { this.patch =  patch }

    var patch:TexturePatch? = null
        private set

    val mutablePatches:MutableMap<String, TexturePatch> = mutableMapOf()
    val patches:Map<String, TexturePatch> = mutablePatches
    val names:Set<String>
        get() = patches.keys

    operator fun get(name:String):TexturePatch? = patches[name]

    fun findName(predicate:(name:String) -> Boolean):String? {
        for(name in names)
            if(predicate(name))
                return name

        return null
    }

    /** Result is sorted */
    fun findNames(result:MutableList<String>? = null, predicate:(name:String) -> Boolean):List<String> {
        val out = result ?: mutableListOf()

        for(name in names) {
            if(! predicate(name))
                continue

            val i = out.binarySearch { it.compareTo(name) }
            out.add(i, name)
        }

        return out
    }

    fun findPatch(predicate:(String, TexturePatch) -> Boolean):TexturePatch? {
        for((name, patch) in patches)
            if(! predicate(name, patch))
                return patch

        return null
    }

    /** Result is sorted */
    fun findPatches(result:MutableList<TexturePatch>? = null, predicate:(String, TexturePatch) -> Boolean):List<TexturePatch> {
        val out = result ?: mutableListOf()

        Pool.get<PoolableStringList>().use { tmpNames ->
            for((name, patch) in patches) {
                if(! predicate(name, patch))
                    continue

                val i = tmpNames.binarySearch { it.compareTo(name) }
                tmpNames.add(i, name)
                out.add(i, patch)
            }

            return out
        }
    }

    override fun dispose() { patch?.dispose() ?: texture.dispose() }

    internal fun addPatch(name:String, region:Rectangle, frame:Rectangle? = null, vertices:FloatArray? = null, indices:ShortArray? = null, transform:TexturePatch.Transform = TexturePatch.Transform.NONE, scaleMultiplier:Float = 1f):TexturePatch {
        val newPatch = when(patch) {
            null -> when(frame) {
                null -> when(vertices) {
                    null -> TexturePatch(texture, region, transform = transform, scale = scale * scaleMultiplier)
                    else -> TexturePatch(texture, region, vertices = vertices, indices = indices ?: throw IllegalArgumentException("provide both vertices and indices or none"), transform = transform, scale = scale * scaleMultiplier)
                }
                else -> when(vertices) {
                    null -> TexturePatch(texture, region, frame = frame, transform = transform, scale = scale * scaleMultiplier)
                    else -> TexturePatch(texture, region, frame = frame, vertices = vertices, indices = indices ?: throw IllegalArgumentException("provide both vertices and indices or none"), transform = transform, scale = scale * scaleMultiplier)
                }
            }
            else -> when(frame) {
                null -> when(vertices) {
                    null -> TexturePatch(patch!!, region, transform = transform, scaleMultiplier = scaleMultiplier)
                    else -> TexturePatch(patch!!, region, vertices = vertices, indices = indices ?: throw IllegalArgumentException("provide both vertices and indices or none"), transform = transform, scaleMultiplier = scaleMultiplier)
                }
                else -> when(vertices) {
                    null -> TexturePatch(patch!!, region, frame = frame, transform = transform, scaleMultiplier = scaleMultiplier)
                    else -> TexturePatch(patch!!, region, frame = frame, vertices = vertices, indices = indices ?: throw IllegalArgumentException("provide both vertices and indices or none"), transform = transform, scaleMultiplier = scaleMultiplier)
                }
            }
        }

        mutablePatches[name] = newPatch
        return newPatch
    }
}
