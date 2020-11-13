package dev.volix.rewinside.odyssey.common.frames.color

import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * Color palette which keeps all colors as is.
 *
 * @author Benedikt Wüller
*/
class DefaultColorTransformer : ColorTransformer() {

    override fun convert(image: BufferedImage, section: Rectangle) = Unit

    override fun convert(rgb: Int) = rgb

}
