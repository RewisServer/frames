package dev.volix.rewinside.odyssey.common.frames.component

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point

import java.awt.Rectangle

/**
 * Components are the building blocks of every frame. Every component has a [position] and [dimensions] which define its bounds. It can manually be
 * marked as [dirty] or does so automatically, when its bounds change in any way. A component marked as [dirty] will be re-rendered in the next render
 * cycle. Every other component intersecting with the bounds of a dirty component will be (fully or partially) re-rendered as well.
 *
 * @author Benedikt Wüller
*/
abstract class Component(val position: Point, val dimensions: Dimension) {

    private var previousBounds: Rectangle? = null

    /**
     * Determines whether the component has changed since the last update.
     */
    open var dirty: Boolean = false
        get() = field || this.previousBounds != null && this.previousBounds != this.calculateBounds()

    /**
     * @return a new instance of the current bounding box of this object.
     */
    fun calculateBounds() = Rectangle(this.position, this.dimensions)

    open fun resetPreviousBounds() {
        this.previousBounds = this.calculateBounds()
    }

    /**
     * @return the sections, which updated since the last render call.
     * Marks this object as non-dirty.
     */
    open fun calculateSectionsToRender(dry: Boolean = false): List<Rectangle> {
        if (!this.dirty) return listOf()

        val bounds = this.calculateBounds()
        val sections = mutableListOf(bounds)

        val previousBounds = this.previousBounds // Copy variable for thread safety.
        if (previousBounds != null && bounds != previousBounds && !bounds.contains(previousBounds)) {
            sections.add(Rectangle(previousBounds))
        }

        if (!dry) {
            this.dirty = false
            this.resetPreviousBounds()
        }

        return sections
    }

    /**
     * Renders the intersecting area with [bounds] to the given [context]
     * relative to the object's position.
     */
    fun render(context: Graphics2D, bounds: Rectangle) {
        val intersection = this.calculateBounds().intersection(bounds)
        intersection.translate(-this.position.x, -this.position.y)
        if (intersection.isEmpty) return

        context.translate(this.position.x, this.position.y)
        this.onRender(context, intersection)
    }

    /**
     * Called when rendering this component.
     *
     * @param context the context to render to.
     * @param bounds the relative bounds of the section to render.
     */
    protected abstract fun onRender(context: Graphics2D, bounds: Rectangle)

}
