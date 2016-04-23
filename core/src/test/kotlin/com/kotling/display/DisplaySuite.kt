package com.kotling.display

import com.kotling.display.test.ContainerBoundsTest
import com.kotling.display.test.ContainerChildrenTest
import com.kotling.display.test.DisplayTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(DisplayTest::class, ContainerChildrenTest::class, ContainerBoundsTest::class)
class DisplaySuite {}
