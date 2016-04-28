package com.kotling.rendering.test

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.kotling.rendering.Shader
import com.kotling.rendering.ShaderProgramCache
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ShaderTest {
    object fakeAppListener : ApplicationAdapter() {}

    companion object {
        lateinit var application:Application

        // this is needed for LibGDX internals to work (e.g. for shaders creation)
        @BeforeClass @JvmStatic fun setUpBeforeClass() {
            Gdx.gl20 = Mockito.mock(GL20::class.java)

            val config:HeadlessApplicationConfiguration = HeadlessApplicationConfiguration()
            application = HeadlessApplication(fakeAppListener, config)
        }

        @AfterClass @JvmStatic fun tearDownAfterClass() {
            application.exit()
        }
    }

    @Test fun testBasic() {
        val subjectA = TestSubjectA()
        val subjectB = TestSubjectB()

        assertEquals(subjectA.colorShader, subjectB.colorShader)
        assertEquals(subjectA.textureShader, subjectB.textureShader)

        assertNotEquals(subjectA.colorShader, subjectB.textureShader)
        assertNotEquals(subjectA.textureShader, subjectB.colorShader)
    }

    class TestSubjectA {
        @Shader("shaders/Colored")
        val colorShader:ShaderProgram by ShaderProgramCache

        @Shader("shaders/Textured")
        val textureShader:ShaderProgram by ShaderProgramCache
    }

    class TestSubjectB {
        @Shader("shaders/Colored")
        val colorShader:ShaderProgram by ShaderProgramCache

        @Shader("shaders/Textured")
        val textureShader:ShaderProgram by ShaderProgramCache
    }
}
