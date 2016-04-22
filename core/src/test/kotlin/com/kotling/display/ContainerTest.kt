package com.kotling.display

import junit.framework.TestCase

class ContainerTest : TestCase() {
    lateinit var container:Container
    lateinit var displays:Array<Display>
    val names = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n")

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

        for(i in displays.indices) {
            val display = displays[i]
            container.children.add(0, display)
            assertEquals(i + 1, container.children.size)
            assertTrue(container.children.contains(display))
            assertEquals(container, display.parent)
            assertEquals(container, display.base)
            assertEquals(null, display.stage)
        }

        container.children.clear()
        assertEquals(0, container.children.size)

        for(i in displays.indices) {
            val display = displays[i]
            container.children.add(container.children.size, display)
            assertEquals(i + 1, container.children.size)
            assertTrue(container.children.contains(display))
            assertEquals(container, display.parent)
            assertEquals(container, display.base)
            assertEquals(null, display.stage)
        }

        container.children.clear()
        assertEquals(0, container.children.size)

        for(i in displays.indices) {
            val display = displays[i]
            container.children.add(i / 2, display)
            assertEquals(i + 1, container.children.size)
            assertTrue(container.children.contains(display))
            assertEquals(container, display.parent)
            assertEquals(container, display.base)
            assertEquals(null, display.stage)
        }
    }

    fun testRemoveAt() {
        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices) {
            val display = container.children.removeAt(0)
            assertEquals(displays.size - i - 1, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }

        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices) {
            val display = container.children.removeAt(container.children.lastIndex)
            assertEquals(displays.size - i - 1, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }

        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices) {
            val display = container.children.removeAt(container.children.lastIndex / 2)
            assertEquals(displays.size - i - 1, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }
    }

    fun testRemoveFromParent() {
        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices) {
            val display = displays[i]
            display.removeFromParent()
            assertEquals(displays.size - i - 1, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }

        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices.reversed()) {
            val display = displays[i]
            display.removeFromParent()
            assertEquals(i, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }

        assertEquals(0, container.children.size)
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        for(i in displays.indices.reversed()) {
            val display = container.children[(i + i / 2) % container.children.size]
            display.removeFromParent()
            assertEquals(i, container.children.size)
            assertFalse(container.children.contains(display))
            assertEquals(null, display.parent)
            assertEquals(display, display.base)
            assertEquals(null, display.stage)
        }
    }

    fun testSet() {
        val odd     = displays.filterIndexed { i, display -> i % 2 != 0 }
        val even    = displays.filterIndexed { i, display -> i % 2 == 0 }

        assertEquals(0, container.children.size)
        container.children.addAll(odd)
        assertEquals(odd.size, container.children.size)

        for(i in 0..Math.min(odd.lastIndex, even.lastIndex)) {
            container.children[i] = even[i]

            assertEquals(odd.size, container.children.size)
            assertFalse(container.children.contains(odd[i]))
            assertTrue(container.children.contains(even[i]))
            assertEquals(null, odd[i].parent)
            assertEquals(odd[i], odd[i].base)
            assertEquals(null, odd[i].stage)
            assertEquals(container, even[i].parent)
            assertEquals(container, even[i].base)
            assertEquals(null, even[i].stage)
        }

        container.children.clear()
        assertEquals(0, container.children.size)

        container.children.addAll(even)
        assertEquals(even.size, container.children.size)

        for(i in 0..Math.min(odd.lastIndex, even.lastIndex)) {
            container.children[i] = odd[i]

            assertEquals(even.size, container.children.size)
            assertFalse(container.children.contains(even[i]))
            assertTrue(container.children.contains(odd[i]))
            assertEquals(null, even[i].parent)
            assertEquals(even[i], even[i].base)
            assertEquals(null, even[i].stage)
            assertEquals(container, odd[i].parent)
            assertEquals(container, odd[i].base)
            assertEquals(null, odd[i].stage)
        }
    }
}