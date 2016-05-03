package com.kotling.display

import com.badlogic.gdx.math.Rectangle
import com.kotling.display.mesh.MeshAttribute
import com.kotling.rendering.Indices
import com.kotling.rendering.Painter
import com.kotling.rendering.Vertices
import com.kotling.style.Style

class MeshDisplay(val vertices:Vertices, val indices:Indices, val style:Style) : Display() {
    constructor(vertices:Vertices, indices:Indices) : this(vertices, indices, Style.createStyle(vertices.attributes))

    var meshAttributes = mutableListOf<MeshAttribute>()

    override fun getBounds(targetSpace:Display?, result:Rectangle?):Rectangle {
        throw UnsupportedOperationException()
    }

    override fun render(painter:Painter) {
        throw UnsupportedOperationException()
    }
}
