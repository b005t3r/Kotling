package com.kotling.display

import junit.framework.TestCase

class ContainerTest : TestCase() {
    lateinit var container:Container
    lateinit var displays:Array<Display>
    val names = arrayOf("a", "b", "c", "d", "e")

    override fun setUp() {
        container = TestContainer()
        displays = Array(names.size, fun(i:Int):Display {
            val d = TestDisplay()
            d.name = names[i]
            return d
        })
    }

    fun testAdd() {
        assertEquals(0, container.children.size)

        displays.forEach {
            container.children.add(it)
            assertEquals(displays.indexOf(it) + 1, container.children.size)
            assertTrue(container.children.contains(it))
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }
    }

    fun testAddAll() {
        assertEquals(0, container.children.size)

        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)
        container.children.forEach {
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }
    }

    fun testRemove() {
        assertEquals(0, container.children.size)

        container.children.addAll(displays)

        displays.forEach {
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)

            container.children.remove(it)

            assertEquals(displays.size - displays.indexOf(it) - 1, container.children.size)
            assertFalse(container.children.contains(it))
            assertEquals(null, it.parent)
            assertEquals(it, it.base)
            assertEquals(null, it.stage)
        }
    }

    fun testRemoveAll() {
        assertEquals(0, container.children.size)

        container.children.addAll(displays)

        displays.forEach {
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }

        val subDisplays = displays.asList().subList(1, 4)

        container.children.removeAll(subDisplays)

        assertEquals(displays.size - subDisplays.size, container.children.size)

        subDisplays.forEach {
            assertFalse(container.children.contains(it))
            assertEquals(null, it.parent)
            assertEquals(it, it.base)
            assertEquals(null, it.stage)
        }

        val leftovers = mutableListOf(*displays)
        leftovers.removeAll(subDisplays)

        leftovers.forEach {
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }
    }

    fun testRetainAll() {
        assertEquals(0, container.children.size)

        container.children.addAll(displays)

        displays.forEach {
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }

        val subDisplays = displays.asList().subList(1, 4)

        container.children.retainAll(subDisplays)

        assertEquals(subDisplays.size, container.children.size)

        subDisplays.forEach {
            assertTrue(container.children.contains(it))
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }

        val leftovers = mutableListOf(*displays)
        leftovers.removeAll(subDisplays)

        leftovers.forEach {
            assertFalse(container.children.contains(it))
            assertEquals(null, it.parent)
            assertEquals(it, it.base)
            assertEquals(null, it.stage)
        }
    }

    fun testAddAt() {
        assertEquals(0, container.children.size)

        displays.forEach {
            container.children.add(it)
            assertEquals(displays.indexOf(it) + 1, container.children.size)
            assertTrue(container.children.contains(it))
            assertEquals(container, it.parent)
            assertEquals(container, it.base)
            assertEquals(null, it.stage)
        }
    }
}