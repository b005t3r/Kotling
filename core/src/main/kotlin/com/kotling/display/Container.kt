package com.kotling.display

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotling.poolable.use
import com.kotling.rendering.Painter

abstract class Container : Display() {
    inner class DisplayList(val l : MutableList<Display>) : MutableList<Display> by l {
        override fun add(element:Display):Boolean {
            add(size, element)
            return true
        }

        override fun add(index:Int, element:Display) {
            if(index < 0 || index > children.size)
                throw IllegalArgumentException("invalid index: $index")

            requiresRedraw = true

            if(element.parent == this@Container) {
                var oldIndex:Int = indexOfFirst { element == it }
                if (oldIndex == index)
                    return

                if (oldIndex == -1)
                    throw IllegalArgumentException("child of this Container but couldn't be found?")

                l.removeAt(oldIndex);
                l.add(if(index < oldIndex) index else index - 1, element);
            }
            else {
                l.add(index, element)

                element.removeFromParent()
                element.parent = this@Container
                //element.addedToParent.dispatch()

                /*
                if(stage != null) {
                    if(element is Container)    element.addedToStageEvent.broadcast()
                    else                        element.addedToStageEvent.dispatch()
                }
                */
            }
        }

        override fun addAll(index:Int, elements:Collection<Display>):Boolean {
            var i = index
            elements.forEach { add(i++, it) }
            return true
        }

        override fun addAll(elements:Collection<Display>):Boolean {
            elements.forEach { add(it) }
            return true
        }

        override fun set(index:Int, element:Display):Display {
            if(get(index) == element)
                return element

            removeAt(index)
            add(index, element)

            return element
        }

        override fun remove(element:Display):Boolean = remove(element, false)
        fun remove(element:Display, dispose:Boolean):Boolean {
            var index = indexOfFirst { it == element }
            if(index < 0)
                return false

            removeAt(index, dispose)
            return true
        }

        override fun removeAt(index:Int):Display = removeAt(index, false)
        fun removeAt(index:Int, dispose:Boolean):Display {
            if(index < 0 || index > size)
                throw IllegalArgumentException("invalid index: $index")

            requiresRedraw = true

            val child = get(index)

            /*
            if(stage != null) {
                if(child is Container)  child.removedFromStageEvent.broadcast()
                else                    child.removedFromStageEvent.dispatch()
            }
            */

            //child.removedFromParent.dispatch()
            child.parent = null
            l.remove(child)

            if(dispose)
                child.dispose()

            return child
        }

        override fun clear() = clear(false)
        fun clear(dispose:Boolean) {
            for(i in indices.reversed())
                removeAt(i, dispose)
        }

        override fun removeAll(elements:Collection<Display>):Boolean = removeAll(elements, false)
        fun removeAll(elements:Collection<Display>, dispose:Boolean):Boolean {
            var result = false
            elements.forEach { result = remove(it, dispose) || result }
            return result
        }

        override fun retainAll(elements:Collection<Display>):Boolean = retainAll(elements, false)
        fun retainAll(elements:Collection<Display>, dispose:Boolean):Boolean {
            var result = false

            for(i in indices.reversed()) {
                if(elements.contains(this[i]))
                    continue

                result = true
                removeAt(i, dispose)
            }

            return result
        }

        override fun iterator():MutableIterator<Display> = listIterator()
        override fun listIterator():MutableListIterator<Display> = listIterator(0)
        override fun listIterator(index:Int):MutableListIterator<Display> = DisplayListIterator(l.listIterator(index))

        override fun subList(fromIndex:Int, toIndex:Int):MutableList<Display> = throw UnsupportedOperationException()

        override fun equals(other:Any?):Boolean = l.equals(other)
        override fun hashCode():Int = l.hashCode()
        override fun toString():String = l.toString()

        inner class DisplayListIterator(val iterator : MutableListIterator<Display>) : MutableListIterator<Display> by iterator {
            lateinit var lastElement:Display

            override fun previous():Display {
                lastElement = iterator.previous()
                return lastElement
            }

            override fun next():Display {
                lastElement = iterator.next()
                return lastElement
            }

            override fun add(element:Display) {
                if(element.parent == this@Container)
                    throw IllegalArgumentException("adding an element already added to this Container is not possible")

                requiresRedraw = true

                iterator.add(element)

                element.removeFromParent()
                element.parent = this@Container
                //element.addedToParent.dispatch()

                /*
                if(stage != null) {
                    if(child is Container)  child.addedToStageEvent.broadcast()
                    else                    child.addedToStageEvent.dispatch()
                }
                */
            }

            override fun remove() {
                requiresRedraw = true

                /*
                if(stage != null) {
                    if(child is Container)  child.removedFromStageEvent.broadcast()
                    else                    child.removedFromStageEvent.dispatch()
                }
                */

                //lastElement.removedFromParent.dispatch()
                lastElement.parent = null

                iterator.remove()
            }

            override fun set(element:Display) {
                if(lastElement == element)
                    return

                if(element.parent == this@Container)
                    throw IllegalArgumentException("setting an element already added to this Container is not possible")

                iterator.set(element)

                /*
                if(stage != null) {
                    if(lastElement is Container)    lastElement.removedFromStageEvent.broadcast()
                    else                            lastElement.removedFromStageEvent.dispatch()
                }
                */

                //lastElement.removedFromParent.dispatch()
                lastElement.parent = null

                element.removeFromParent()
                element.parent = this@Container
                //element.addedToParent.dispatch()

                /*
                if(stage != null) {
                    if(child is Container)  child.addedToStageEvent.broadcast()
                    else                    child.addedToStageEvent.dispatch()
                }
                */
            }
        }
    }

    val children:DisplayList = DisplayList(mutableListOf())

    var touchGroup = false

    override fun dispose() {
        children.forEach { it.dispose() }
        super.dispose()
    }

    override fun getBounds(targetSpace:Display?, result:Rectangle?):Rectangle {
        val out = result ?: Rectangle()

        when(children.size) {
            0 -> {
                Pool.Matrix3.use { m ->
                Pool.Vector2.use { p ->
                    getTransformationMatrix(targetSpace, m)
                    return out.setPosition(p.set(0f, 0f).mul(m)).setSize(0f, 0f)
                }}
            }
            1 -> {
                return children[0].getBounds(targetSpace, out)
            }
            else -> {
                Pool.Rectangle.use { r ->
                    var first = true
                    children.forEach {
                        if(first) {
                            it.getBounds(targetSpace, out)
                            first = false
                        }
                        else {
                            it.getBounds(targetSpace, r)
                            out.merge(r)
                        }
                    }

                    return out
                }
            }
        }
    }

    override fun hitTest(localPoint:Vector2):Display? {
        if (! visible || ! touchable /*|| ! hitTestMask(localPoint)*/) return null

        if(children.isEmpty())
            return null

        var target:Display? = null

        Pool.Matrix3.use { m ->
        Pool.Vector2.use { p ->
            for(i in children.lastIndex downTo 0) {
                val child = children[i]

                // if(child.isMask) continue

                target = child.hitTest(p.set(localPoint).mul(m.mul(child.transformationMatrix).inv()))

                if(target != null)
                    break
            }

            return if(touchGroup) this else target
        }}
    }

    override fun render(painter:Painter) {
        // TODO:
    }
}
