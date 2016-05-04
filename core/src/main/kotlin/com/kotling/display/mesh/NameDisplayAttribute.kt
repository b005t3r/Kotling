package com.kotling.display.mesh

import com.kotling.display.Display

interface NameDisplayAttribute : DisplayAttribute {
    val name:String
}

var Display.name:String
    get() = (attributes.find { it is NameDisplayAttribute } as? NameDisplayAttribute)?.name ?: ""
    set(value) {
        val attr = attributes.find { it is NameDisplayAttribute } as? NameDisplayAttribute

        if(attr != null) {
            if(attr.name == value)
                return

            attributes.remove(attr)
        }

        if(value == "")
            return

        attributes.add(object : NameDisplayAttribute {
            override val name:String
                get() = value
        })
    }
