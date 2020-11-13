package dev.volix.rewinside.odyssey.common.frames.component

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle

/**
 * @author Benedikt Wüller
 */
open class DummyComponent(position: Point, dimensions: Dimension) : Component(position, dimensions) {

    override fun onRender(context: Graphics2D, bounds: Rectangle) = Unit

}
