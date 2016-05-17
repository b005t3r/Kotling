package com.kotling.display.test

import com.kotling.display.Container
import com.kotling.display.Display
import com.kotling.display.attribute.name
import junit.framework.TestCase

class ContainerChildrenTest: TestCase() {
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

    fun testGetSet() {
        val odd     = displays.filterIndexed { i, display -> i % 2 != 0 }
        val even    = displays.filterIndexed { i, display -> i % 2 == 0 }

        assertEquals(0, container.children.size)
        container.children.addAll(odd)
        assertEquals(odd.size, container.children.size)

        for(i in 0..Math.min(odd.lastIndex, even.lastIndex)) {
            assertEquals(odd[i], container.children[i])
            container.children[i] = even[i]

            assertEquals(odd.size, container.children.size)
            assertFalse(container.children.contains(odd[i]))
            assertTrue(container.children.contains(even[i]))
            assertEquals(even[i], container.children[i])
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
            assertEquals(even[i], container.children[i])
            container.children[i] = odd[i]

            assertEquals(even.size, container.children.size)
            assertFalse(container.children.contains(even[i]))
            assertTrue(container.children.contains(odd[i]))
            assertEquals(odd[i], container.children[i])
            assertEquals(null, even[i].parent)
            assertEquals(even[i], even[i].base)
            assertEquals(null, even[i].stage)
            assertEquals(container, odd[i].parent)
            assertEquals(container, odd[i].base)
            assertEquals(null, odd[i].stage)
        }
    }

    fun testIteratorNext() {
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        with(container.children.listIterator()) {
            for (i in displays.indices) {
                assertTrue(hasNext())
                assertEquals(displays[i], next())
            }

            assertFalse(hasNext())
        }
    }

    fun testIteratorPrev() {
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        with(container.children.listIterator(container.children.size)) {
            for (i in displays.indices.reversed()) {
                assertTrue(hasPrevious())
                assertEquals(displays[i], previous())
            }

            assertFalse(hasPrevious())
        }
    }

    fun testIteratorRemove() {
        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        with(container.children.listIterator()) {
            for (i in displays.indices) {
                assertTrue(container.children.contains(displays[i]))
                assertEquals(container, displays[i].parent)
                next()
                remove()
                assertFalse(container.children.contains(displays[i]))
                assertEquals(null, displays[i].parent)
            }

            assertFalse(hasNext())
        }

        container.children.addAll(displays)
        assertEquals(displays.size, container.children.size)

        with(container.children.listIterator(container.children.size)) {
            for (i in displays.indices.reversed()) {
                assertTrue(container.children.contains(displays[i]))
                assertEquals(container, displays[i].parent)
                previous()
                remove()
                assertFalse(container.children.contains(displays[i]))
                assertEquals(null, displays[i].parent)
            }

            assertFalse(hasPrevious())
        }
    }

    fun testIteratorAdd() {
        val odd     = displays.filterIndexed { i, display -> i % 2 != 0 }
        val even    = displays.filterIndexed { i, display -> i % 2 == 0 }

        assertEquals(0, container.children.size)
        container.children.addAll(odd)
        assertEquals(odd.size, container.children.size)

        with(container.children.listIterator()) {
            for (i in 0..Math.min(odd.lastIndex, even.lastIndex)) {
                assertTrue(container.children.contains(odd[i]))
                assertFalse(container.children.contains(even[i]))
                assertEquals(container, odd[i].parent)
                assertEquals(null, even[i].parent)
                add(even[i])
                assertEquals(container, odd[i].parent)
                assertEquals(container, even[i].parent)
                assertTrue(container.children.contains(odd[i]))
                assertTrue(container.children.contains(even[i]))
                next()
            }
        }
    }

    fun testIteratorSet() {
        val odd     = displays.filterIndexed { i, display -> i % 2 != 0 }
        val even    = displays.filterIndexed { i, display -> i % 2 == 0 }

        assertEquals(0, container.children.size)
        container.children.addAll(odd)
        assertEquals(odd.size, container.children.size)

        with(container.children.listIterator()) {
            for (i in 0..Math.min(odd.lastIndex, even.lastIndex)) {
                assertTrue(container.children.contains(odd[i]))
                assertFalse(container.children.contains(even[i]))
                assertEquals(container, odd[i].parent)
                assertEquals(null, even[i].parent)
                next()
                set(even[i])
                assertFalse(container.children.contains(odd[i]))
                assertTrue(container.children.contains(even[i]))
                assertEquals(null, odd[i].parent)
                assertEquals(container, even[i].parent)
            }
        }

        assertEquals(odd.size, container.children.size)
        container.children.addAll(even)
        assertEquals(even.size, container.children.size)

        with(container.children.listIterator(container.children.size)) {
            for (i in Math.min(odd.lastIndex, even.lastIndex) downTo 0) {
                assertTrue(container.children.contains(even[i]))
                assertFalse(container.children.contains(odd[i]))
                assertEquals(container, even[i].parent)
                assertEquals(null, odd[i].parent)
                previous()
                set(odd[i])
                assertFalse(container.children.contains(even[i]))
                assertTrue(container.children.contains(odd[i]))
                assertEquals(null, even[i].parent)
                assertEquals(container, odd[i].parent)
            }
        }
    }
}
