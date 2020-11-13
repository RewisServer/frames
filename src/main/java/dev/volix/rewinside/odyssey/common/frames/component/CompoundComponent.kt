package dev.volix.rewinside.odyssey.common.frames.component

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle

/**
 * @author Benedikt Wüller
 */
open class CompoundComponent(position: Point, dimensions: Dimension) : Component(position, dimensions) {

    protected open val components = mutableListOf<Component?>()
    protected open val removedComponents = mutableListOf<Component?>()

    override var dirty: Boolean
        set(value) = this.components.forEach { it?.dirty = value }
        get() = this.components.any { it?.dirty ?: false }

    open fun addComponent(component: Component?) {
        this.components.add(component)
        component?.dirty = true
    }

    open fun addComponentBelow(component: Component?, other: Component) {
        val index = this.components.indexOf(other)
        this.components.add(index, component)
        component?.dirty = true
    }

    open fun removeComponent(component: Component?) {
        this.removedComponents.add(component)
        component?.dirty = true
    }

    fun clear() = this.components.forEach { this.removeComponent(it) }

    override fun onRender(context: Graphics2D, bounds: Rectangle) {
        this.components
                .filterNot { this.removedComponents.contains(it) }
                .forEach { it?.render(context.create() as Graphics2D, bounds) }
    }

    override fun calculateSectionsToRender(dry: Boolean): List<Rectangle> {
        if (!this.dirty) return listOf()

        val sections = this.components
                .filterNotNull()
                .map { it.calculateSectionsToRender(dry) }
                .reduce { a, b -> a.union(b).toList() }
                .map { Rectangle(it.x + this.position.x, it.y + this.position.y, it.width, it.height) }

        if (!dry) {
            this.components.removeAll(this.removedComponents)
            this.removedComponents.clear()
        }

        return sections
    }

    override fun resetPreviousBounds() = this.components.forEach { it?.resetPreviousBounds() }

}
