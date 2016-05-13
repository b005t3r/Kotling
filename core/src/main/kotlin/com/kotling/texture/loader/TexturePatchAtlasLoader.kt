package com.kotling.texture.loader

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.kotling.texture.TexturePatch
import com.kotling.texture.TexturePatchAtlas

class TexturePatchAtlasLoader(resolver:FileHandleResolver) : SynchronousAssetLoader<TexturePatchAtlas, TexturePatchAtlasLoader.TexturePatchAtlasParameters>(resolver) {
    override fun getDependencies(fileName:String?, file:FileHandle?, parameter:TexturePatchAtlasParameters?):Array<AssetDescriptor<*>>? {
        when (parameter) {
            null -> {
                val atlasJsonReader = JsonReader()
                val atlasJson       = atlasJsonReader.parse(file)
                val texturePath     = atlasJson.get("imagePath").asString()
                val atlasDir        = file!!.parent()
                val descriptor      = Array<AssetDescriptor<*>>()
                descriptor.add(AssetDescriptor(atlasDir.child(texturePath), Texture::class.java))

                return descriptor
            }
            else -> {
                val descriptor = Array<AssetDescriptor<*>>()
                descriptor.add(AssetDescriptor(parameter.atlasName, TexturePatchAtlas::class.java))

                return descriptor
            }
        }
    }

    override fun load(assetManager:AssetManager?, fileName:String?, file:FileHandle?, parameter:TexturePatchAtlasParameters?):TexturePatchAtlas? {
        val atlasJsonReader = JsonReader()
        val atlasJson       = atlasJsonReader.parse(file)
        val imagePath       = atlasJson.get("imagePath").asString()

        when (parameter) {
            null -> {
                val atlasDir    = file!!.parent()
                val textureFile = atlasDir.child(imagePath)
                val texture     = assetManager!!.get(textureFile.path(), Texture::class.java)
                val atlas       = TexturePatchAtlas(texture)

                populateAtlas(atlas, atlasJson)

                return atlas
            }
            else -> {
                val parentAtlas = assetManager!!.get(parameter.atlasName, TexturePatchAtlas::class.java)
                val parentPatch = parentAtlas[imagePath] ?: throw IllegalStateException("parent atlas ${parameter.atlasName} does not contain a patch $imagePath")
                val atlas       = TexturePatchAtlas(parentPatch)

                populateAtlas(atlas, atlasJson)

                return atlas
            }
        }
    }

    private fun populateAtlas(atlas:TexturePatchAtlas, atlasJson:JsonValue) {
        atlasJson.get("texturePatches").forEach { patchJson ->
            val name = patchJson.get("name").asString()
            val rotated = patchJson.get("rotated").asBoolean()
            val regionJson = patchJson.get("region")
            val frameJson = patchJson.get("frame")
            val verticesJson = patchJson.get("vertices")
            val indicesJson = patchJson.get("indices")

            val region = Rectangle(regionJson.get("x").asFloat(), regionJson.get("y").asFloat(), regionJson.get("w").asFloat(), regionJson.get("h").asFloat())
            val frame = if (frameJson != null) Rectangle(frameJson.get("x").asFloat(), frameJson.get("y").asFloat(), frameJson.get("w").asFloat(), frameJson.get("h").asFloat()) else null
            val vertices = verticesJson?.asFloatArray()
            val indices = indicesJson?.asShortArray()

            atlas.addPatch(name, region, frame, vertices, indices, if (rotated) TexturePatch.Transform.CLOCKWISE else TexturePatch.Transform.NONE)
        }
    }

    class TexturePatchAtlasParameters(val atlasName:String) : AssetLoaderParameters<TexturePatchAtlas>()
}
