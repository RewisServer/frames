package dev.volix.rewinside.odyssey.common.frames.resource.image

import dev.volix.rewinside.odyssey.common.frames.helper.getTile
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage

/**
 * @author Benedikt Wüller
 */
class SpriteSheet(val image: BufferedImage, val spriteWidth: Int, val spriteHeight: Int) {

    private val sprites = mutableMapOf<Int, BufferedImage>()

    val columns = this.image.width / this.spriteWidth
    val rows = this.image.height / this.spriteHeight

    val spriteDimensions = Dimension(this.spriteWidth, this.spriteHeight)

    fun getSprite(tile: Point) = this.getSprite(tile.x, tile.y)

    fun getSprite(x: Int, y: Int) = this.getSprite(y * this.columns + x)

    fun getSprite(index: Int): BufferedImage {
        val spriteCount = this.rows * this.columns
        if (index !in 0..spriteCount) {
            throw IndexOutOfBoundsException("The index has to be between 0 and " + (spriteCount - 1) + ": " + index)
        }

        return this.sprites.getOrPut(index) {
            val x = index % this.columns
            val y = index / this.columns
            return this.image.getTile(x, y, this.spriteWidth, this.spriteHeight)
        }
    }

    operator fun get(index: Int) = this.getSprite(index)

}
