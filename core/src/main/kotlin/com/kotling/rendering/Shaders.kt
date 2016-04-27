package com.kotling.rendering

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kotlin.reflect.KProperty

@Target(AnnotationTarget.PROPERTY)
annotation class Shader(val pathPrefix:String)

object ShaderProgramCache {
    private val shaders = mutableMapOf<String, ShaderProgram>()

    fun fromAnnotation(shader:Shader):ShaderProgram = fromPath(shader.pathPrefix)

    fun fromPath(pathPrefix:String):ShaderProgram {
        var shader = shaders[pathPrefix]

        if(shader != null)
            return shader

        shader = ShaderProgram(FileHandle(pathPrefix + ".vert"), FileHandle(pathPrefix + ".frag"))
        shaders[pathPrefix] = shader

        return shader
    }

    operator fun getValue(thisRef:Any?, property:KProperty<*>):ShaderProgram {
        val shader:Shader = property.annotations.find { it is Shader } as Shader? ?: throw UninitializedPropertyAccessException("property not annotated with @Shader")

        return ShaderProgramCache.fromAnnotation(shader)
    }
}
