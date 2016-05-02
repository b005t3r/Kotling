package com.kotling.rendering

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import kotlin.reflect.KProperty

@Target(AnnotationTarget.PROPERTY)
annotation class VertexFormat(vararg val format:String = arrayOf("position:float2"))

object VertexAttributesCache {
    private val componentCountPerType = mutableMapOf("float1" to 1, "float2" to 2, "float3" to 3, "float4" to 4, "byte4" to 4)
    private val attributes:MutableMap<Array<out String>, VertexAttributes> = mutableMapOf()

    fun fromAnnotation(format:VertexFormat):VertexAttributes = fromString(*format.format)

    fun fromString(vararg format:String):VertexAttributes {
        if(format.size == 0)
            throw IllegalArgumentException("invalid format: $format")

        var attrs:VertexAttributes? = attributes[format]

        if(attrs != null)
            return attrs

        attrs = create(*format)
        attributes[format] = attrs

        return attrs
    }

    operator fun getValue(thisRef:Any?, property:KProperty<*>):VertexAttributes {
        val vertexFormat:VertexFormat = property.annotations.find { it is VertexFormat } as VertexFormat? ?: throw UninitializedPropertyAccessException("property not annotated with @VertexFormat")

        return VertexAttributesCache.fromAnnotation(vertexFormat)
    }

    private fun create(vararg format:String):VertexAttributes {
        var hasPosition = false

        var attrs = Array(
            format.size,
            fun(i:Int):VertexAttribute {
                val parts = format[i].split(":")
                val name = parts[0]
                val type = parts[1]
                val usage = getUsage(name, type)

                if(! hasPosition && usage == VertexAttributes.Usage.Position)
                    hasPosition = true

                return when {
                    usage == VertexAttributes.Usage.ColorPacked && type != "byte4" ->
                        throw IllegalArgumentException("invalid type for $name: packed colors have to use 'byte4' type")

                    usage == VertexAttributes.Usage.ColorUnpacked && type != "float4" ->
                        throw IllegalArgumentException("invalid type for $name: unpacked colors have to use 'float4' type")

                    usage == VertexAttributes.Usage.TextureCoordinates && type != "float2" ->
                        throw IllegalArgumentException("invalid type for $name: texture coordinates have to use 'float2' type")

                    usage == VertexAttributes.Usage.Position && type != "float2" ->
                        throw IllegalArgumentException("invalid type for $name: positions have to use 'float2' type")

                    else -> VertexAttribute(usage, getComponentCount(type), name)
                }
            }
        );

        if(! hasPosition)
            throw IllegalArgumentException("position attribute not present in $format")

        return VertexAttributes(*attrs)
    }

    private fun getUsage(name:String, type:String):Int = when {
        name.contains("position", true) -> VertexAttributes.Usage.Position
        name.contains("color", true) && type == "byte4" -> VertexAttributes.Usage.ColorPacked
        name.contains("color", true) && type == "float4" -> VertexAttributes.Usage.ColorUnpacked
        name.contains("uv", true) || name.contains("texCoord", true) -> VertexAttributes.Usage.TextureCoordinates
        else -> VertexAttributes.Usage.Generic
    }

    private fun getComponentCount(type:String):Int = componentCountPerType[type] ?: throw IllegalArgumentException("invalid type: $type, allowed types are ${componentCountPerType.keys}")
}
