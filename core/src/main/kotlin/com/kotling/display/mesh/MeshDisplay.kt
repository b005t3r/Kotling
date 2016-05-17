package com.kotling.display.mesh

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.display.Display
import com.kotling.rendering.Indices
import com.kotling.rendering.Painter
import com.kotling.rendering.Vertices
import com.kotling.style.Style
import com.kotling.util.Pool
import com.kotling.util.poolable.use

open class MeshDisplay(val vertices:Vertices, val indices:Indices, val style:Style) : Display() {
    init { style.mesh = this }
    constructor(vertices:Vertices, indices:Indices) : this(vertices, indices, Style.createStyle(vertices.attributes))

    var pixelSnappingEnabled = false

    override fun hitTest(localPoint:Vector2):Display? {
        if(! visible || ! touchable /* || ! hitTestMask(localPoint) */ || vertices.contains(localPoint, indices))
            return null
        else
            return this
    }

    override fun getBounds(targetSpace:Display?, result:Rectangle?):Rectangle {
        Pool.Matrix3.use { matrix ->
            getTransformationMatrix(targetSpace, matrix)
            return vertices.getBounds(matrix = matrix, result = result)
        }
    }

    override fun render(painter:Painter) {
        // TODO: pixel snapping
        //if(pixelSnappingEnabled)
        //    painter.state.modelViewMatrix.snapToPixels(painter.pixelSize)

        painter.batchMesh(this)
    }
}
