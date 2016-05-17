package com.kotling.display.attribute

import com.badlogic.gdx.graphics.Texture
import com.kotling.display.Display

interface TexturesDisplayAttribute:DisplayAttribute {
    val textures:List<Texture>
}

var Display.textures:List<Texture>
    get() = (attributes.find { it is TexturesDisplayAttribute } as? TexturesDisplayAttribute)?.textures ?: emptyList()
    set(value) {
        val attr = attributes.find { it is TexturesDisplayAttribute } as? TexturesDisplayAttribute

        if(attr != null) {
            if(attr.textures == value)
                return

            attributes.remove(attr)
        }

        if(value == emptyList<Texture>())
            return

        attributes.add(object :TexturesDisplayAttribute {
            override val textures:List<Texture>
                get() = value
        })
    }
