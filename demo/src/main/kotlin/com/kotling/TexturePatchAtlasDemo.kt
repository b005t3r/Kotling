package com.kotling

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.kotling.texture.TexturePatchAtlas
import com.kotling.texture.loader.TexturePatchAtlasLoader

class TexturePatchAtlasDemo: ApplicationAdapter() {
    private lateinit var assetManager:AssetManager
    private lateinit var atlas:TexturePatchAtlas

    private var assetsLoaded = false

    override fun create() {
        assetManager = AssetManager()

        assetManager.setLoader(TexturePatchAtlas::class.java, TexturePatchAtlasLoader(InternalFileHandleResolver()))
        assetManager.load("demo/assets/atlas.json", TexturePatchAtlas::class.java)
    }

    override fun render() {
        if(! assetsLoaded) {
            assetsLoaded = assetManager.update()

            if(assetsLoaded) {
                atlas = assetManager.get("demo/assets/atlas.json", TexturePatchAtlas::class.java)
                val star = atlas["sub/star"]
                val untrimmableVertical = atlas["untrimmable_vertical"]

                println(star)
                println(untrimmableVertical)
            }
        }
        else {

        }
    }
}
