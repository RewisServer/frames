package dev.volix.rewinside.odyssey.common.frames.color

import dev.volix.rewinside.odyssey.common.frames.helper.distanceTo
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.lang.IllegalArgumentException
import kotlin.math.max

/**
 * Converts every color to the closest of the given [colors].
 * If no color could be found, the first color will be returned.
 *
 * @author Benedikt Wüller
*/
open class PaletteColorTransformer(vararg val colors: Color) : ColorTransformer() {

    init {
        if (this.colors.isEmpty()) throw IllegalArgumentException("At least one color must be provided.")
    }

    private val matchedIndices = HashMap<Color, Int>()

    open fun matchColorIndices(image: BufferedImage, section: Rectangle): List<Int> {
        val colors = image.getRGB(section.x, section.y, section.width, section.height, null, 0, section.width)
        return colors.map { this.matchColorIndex(it) }
    }

    /**
     * @return the index of the closest color.
     */
    open fun matchColorIndex(color: Color): Int {
        return this.matchedIndices.getOrPut(color) {
            var bestIndex = -1
            var bestDistance = Double.MAX_VALUE

            for (i in this.colors.indices) {
                val other = this.colors[i]
                if (color == other) return@getOrPut i

                val distance = color.distanceTo(other)
                if (distance >= bestDistance) continue

                bestIndex = i
                bestDistance = distance
            }

            return@getOrPut max(0, bestIndex)
        }
    }

    /**
     * @return the index of the closest color.
     */
    open fun matchColorIndex(rgb: Int) = this.matchColorIndex(Color(rgb, true))

    /**
     * @return the closest [Color].
     */
    open fun matchColor(color: Color) = this.colors[this.matchColorIndex(color)]

    /**
     * @return the closest [Color].
     */
    open fun matchColor(rgb: Int) = this.matchColor(Color(rgb, true))

    override fun convert(rgb: Int) = this.matchColor(rgb).rgb

}
