package com.kotling.rendering

import com.kotling.display.test.ContainerBoundsTest
import com.kotling.display.test.ContainerChildrenTest
import com.kotling.display.test.DisplayTest
import com.kotling.rendering.test.IndicesTest
import com.kotling.rendering.test.ShaderTest
import com.kotling.rendering.test.VertexFormatTest
import com.kotling.rendering.test.VerticesTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(VertexFormatTest::class, ShaderTest::class, IndicesTest::class, VerticesTest::class)
class RenderingSuite
