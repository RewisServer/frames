package dev.volix.rewinside.odyssey.common.frames.component

import dev.volix.rewinside.odyssey.common.frames.helper.fillRect
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle

/**
 * @author Benedikt Wüller
 */
open class ColorComponent(position: Point, dimensions: Dimension, color: Color)
    : Component(position, dimensions) {

    var color = color; set(value) {
        this.dirty = this.dirty || value != field
        field = value
    }

    override fun onRender(context: Graphics2D, bounds: Rectangle) {
        context.color = this.color
        context.fillRect(bounds)
    }

}
