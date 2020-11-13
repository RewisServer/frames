package dev.volix.rewinside.odyssey.common.frames.component

import dev.volix.rewinside.odyssey.common.frames.resource.image.SpriteSheet
import java.awt.Dimension
import java.awt.Point

/**
 * @author Benedikt Wüller
 */
open class SpriteComponent(position: Point, dimensions: Dimension, spriteSheet: SpriteSheet, spriteIndex: Int = -1)
    : ImageComponent(position, dimensions, null) {

    constructor(position: Point, spriteSheet: SpriteSheet, spriteIndex: Int = -1)
            : this(position, spriteSheet.spriteDimensions, spriteSheet, spriteIndex)

    var spriteSheet = spriteSheet; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        if (this.dirty) this.updateImage()
    }

    var spriteIndex = spriteIndex; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        if (this.dirty) this.updateImage()
    }

    init {
        this.updateImage()
    }

    fun setSprite(coordinates: Point) {
        this.spriteIndex = coordinates.y * this.spriteSheet.columns + coordinates.x
    }

    private fun updateImage() {
        if (this.spriteIndex !in 0..(this.spriteSheet.columns * this.spriteSheet.rows)) {
            this.image = null
        } else {
            this.image = this.spriteSheet[this.spriteIndex]
        }
    }

}
