package dev.volix.rewinside.odyssey.common.frames.component

import dev.volix.rewinside.odyssey.common.frames.helper.div
import dev.volix.rewinside.odyssey.common.frames.helper.untiled
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.max
import kotlin.math.min

/**
 * A compound component containing [tiles] (x * y) tiles.
 * The [tileDimensions] are calculated by dividing [dimensions] through [tiles].
 * Each child component is determined by [getTileComponent] and may be `null`.
 *
 * @author Benedikt Wüller
*/
abstract class TiledComponent<T : Component>(position: Point, dimensions: Dimension, val tiles: Dimension)
    : CompoundComponent(position, dimensions) {

    private var initialized = false

    override var dirty: Boolean
        set(value) {
            this.ensureComponentsExist()
            this.components.forEach { it?.dirty = value }
        }
        get() {
            this.ensureComponentsExist()
            return this.components.any { it?.dirty ?: false }
        }

    val tileDimensions = this.dimensions / this.tiles

    private fun ensureComponentsExist() {
        if (this.initialized) return
        this.initialized = true
        this.initTiles()
    }

    /**
     * Generates all tile components.
     */
    private fun initTiles() {
        for (y in 0 until this.tiles.height) {
            for (x in 0 until this.tiles.width) {
                val coordinates = Point(x, y)
                val component = this.getTileComponent(
                        coordinates,
                        coordinates.untiled(this.tileDimensions),
                        this.tileDimensions
                )
                super.addComponent(component)
            }
        }
    }

    override fun calculateSectionsToRender(dry: Boolean): List<Rectangle> {
        this.ensureComponentsExist()
        return super.calculateSectionsToRender(dry)
    }

    /**
     * @return the component at the given [tile] coordinates.
     */
    @Suppress("UNCHECKED_CAST") // We know the type is always castable.
    fun getComponent(tile: Point): T? {
        this.ensureComponentsExist()
        return this.components[tile.y * this.tiles.width + tile.x] as T
    }

    /**
     * Marks the tile at the given [tile] coordinates as [dirty].
     */
    fun setDirty(tile: Point) {
        this.ensureComponentsExist()
        val component = this.getComponent(tile) ?: return
        component.dirty = true
    }

    /**
     * Marks all tiles in the given tile range as [dirty].
     */
    fun setDirty(tileFrom: Point, tileTo: Point) {
        val minX = min(tileFrom.x, tileTo.x)
        val maxX = max(tileFrom.x, tileTo.x)
        val minY = min(tileFrom.y, tileTo.y)
        val maxY = max(tileFrom.y, tileTo.y)

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                this.setDirty(Point(x, y))
            }
        }
    }

    /**
     * @return the tile component at the given [tile] coordinates.
     * @param[position] the 'untiled' coordinates.
     * @param[dimensions] the dimensions of the tile.
     */
    protected abstract fun getTileComponent(tile: Point, position: Point, dimensions: Dimension): T?

}
