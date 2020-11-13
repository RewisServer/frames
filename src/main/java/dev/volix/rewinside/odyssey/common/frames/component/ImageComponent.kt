package dev.volix.rewinside.odyssey.common.frames.component

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * @author Benedikt Wüller
 */
open class ImageComponent(position: Point, dimensions: Dimension, image: BufferedImage?) : Component(position, dimensions) {

    var image = image; set(value) {
        this.dirty = this.dirty || value != field
        field = value
    }

    override fun onRender(context: Graphics2D, bounds: Rectangle) {
        val image = this.image ?: return
        context.drawImage(
                image,
                bounds.x, bounds.y, bounds.maxX.toInt(), bounds.maxY.toInt(),
                bounds.x, bounds.y, bounds.maxX.toInt(), bounds.maxY.toInt(),
                null
        )
    }

    /**
     * Returns `true` if there is at least one pixel intersecting with the image and the corresponding pixel is not fully transparent.
     * Returns `false` if the [image] is `null` or the intersection is empty.
     */
    fun intersectsPixels(section: Rectangle): Boolean {
        if (section.isEmpty) return false

        val intersection: Rectangle = this.calculateBounds().intersection(section)
        if (intersection.isEmpty) return false

        val sprite = this.image ?: return false

        for (dy in 0 until intersection.height) {
            for (dx in 0 until intersection.width) {
                val x = intersection.x + dx - this.position.x
                val y = intersection.y + dy - this.position.y
                val color = Color(sprite.getRGB(x, y), true)
                if (color.alpha > 0) return true
            }
        }

        return false
    }

}
