package com.kotling

import com.kotling.display.DisplaySuite
import com.kotling.rendering.RenderingSuite
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(DisplaySuite::class, RenderingSuite::class)
class KotlingSuite