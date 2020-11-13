package dev.volix.rewinside.odyssey.common.frames.component

import dev.volix.rewinside.odyssey.common.frames.helper.tiled
import dev.volix.rewinside.odyssey.common.frames.helper.untiled
import java.awt.Dimension
import java.awt.Point

/**
 * A component with coordinates based on a tiled coordinate system.
 *
 * @author Benedikt Wüller
*/
abstract class TileBasedComponent(position: Point, dimensions: Dimension, val tileDimensions: Dimension)
    : Component(position, dimensions) {

    protected open val tileOffset = Point()

    fun setTile(coordinates: Point) {
        this.position.location = coordinates.untiled(this.tileDimensions, this.tileOffset)
    }

    fun setTileX(x: Int) {
        this.position.x = x * this.tileDimensions.width + this.tileOffset.x
    }

    fun setTileY(y: Int) {
        this.position.y = y * this.tileDimensions.height + this.tileOffset.y
    }

    fun getTile() = this.position.tiled(this.tileDimensions, this.tileOffset)

    fun getTileX() = (this.position.x - this.tileOffset.x) / this.tileDimensions.width

    fun getTileY() = (this.position.y - this.tileOffset.y) / this.tileDimensions.height

}
