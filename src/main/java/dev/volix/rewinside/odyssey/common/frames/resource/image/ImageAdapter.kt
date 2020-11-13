package dev.volix.rewinside.odyssey.common.frames.resource.image

import java.awt.Dimension
import java.awt.image.BufferedImage

/**
 * @author Benedikt Wüller
 */
interface ImageAdapter {

    /**
     * @return the image with the given [name].
     */
    fun get(name: String): BufferedImage

    /**
     * @return a SpriteSheet with the given [name].
     */
    fun getSheet(name: String, spriteWidth: Int, spriteHeight: Int): SpriteSheet

    /**
     * @return a SpriteSheet with the given [name].
     */
    fun getSheet(name: String, spriteSize: Int) = this.getSheet(name, spriteSize, spriteSize)

    /**
     * @return a SpriteSheet with the given [name].
     */
    fun getSheet(name: String, dimensions: Dimension) = this.getSheet(name, dimensions.width, dimensions.height)

}
