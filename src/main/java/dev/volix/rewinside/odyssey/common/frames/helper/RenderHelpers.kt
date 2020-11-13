package dev.volix.rewinside.odyssey.common.frames.helper

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * @author Benedikt Wüller
 */

fun Graphics2D.fillRect(bounds: Rectangle) = this.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)

fun BufferedImage.getTile(x: Int, y: Int, tileWidth: Int, tileHeight: Int): BufferedImage {
    return this.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight)
}

/**
 * Compares the two colors [this] and [other] and returns the distance between them.
 *
 * @see [this Stackoverflow answer](https://stackoverflow.com/a/6334454).
 *
 * @return the distance between the colors.
 */
fun Color.distanceTo(other: Color): Double {
    if (this.alpha != other.alpha) return Double.MAX_VALUE

    val redMean = (this.red + other.red) / 2.0

    val weightRed = 2.0 + redMean / 256.0
    val weightGreen = 4.0
    val weightBlue = 2.0 + (255.0 - redMean) / 256.0

    val red = (this.red - other.red).squared()
    val green = (this.green - other.green).squared()
    val blue = (this.blue - other.blue).squared()

    return weightRed * red + weightGreen * green + weightBlue * blue
}
