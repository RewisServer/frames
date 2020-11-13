package dev.volix.rewinside.odyssey.common.frames.color

import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min

/**
 * Transforms every pixel of an image or section of an image.
 *
 * @author Benedikt Wüller
 */
abstract class ColorTransformer {

    /**
     * Converts all pixels in the given [section] and [image] to colors supported by this palette.
     */
    open fun convert(image: BufferedImage, section: Rectangle) {
        val minX = max(0, section.x)
        val maxX = min(image.width, section.x + section.width)
        val minY = max(0, section.y)
        val maxY = min(image.height, section.y + section.height)

        for (y in minY until maxY) {
            for (x in minX until maxX) {
                val color = this.convert(image.getRGB(x, y))
                image.setRGB(x, y, color)
            }
        }
    }

    /**
     * Converts all pixels of the given [image] to colors supported by this palette.
     */
    fun convert(image: BufferedImage) = this.convert(image, Rectangle(image.width, image.height))

    /**
     * @return the converted color as rgb value.
     */
    abstract fun convert(rgb: Int): Int

}
