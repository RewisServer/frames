package dev.volix.rewinside.odyssey.common.frames.helper

import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Rectangle2D

/**
 * @author Benedikt Wüller
 */

fun Int.squared() = this * this

fun Point.untiled(dimensions: Dimension, offset: Point = Point()): Point {
    return Point(
            this.x * dimensions.width + offset.x,
            this.y * dimensions.height + offset.y
    )
}

fun Point.tiled(dimensions: Dimension, offset: Point = Point()): Point {
    return Point(
            (this.x - offset.x) / dimensions.width,
            (this.y - offset.y) / dimensions.height
    )
}

operator fun Dimension.div(other: Dimension) = Dimension(this.width / other.width, this.height / other.height)

operator fun Rectangle.times(scale: Dimension) = Rectangle(
        this.x * scale.width, this.y * scale.height,
        this.width * scale.width, this.height * scale.height
)

operator fun Rectangle.div(scale: Dimension) = Rectangle(
        this.x / scale.width, this.y / scale.height,
        this.width / scale.width, this.height / scale.height
)

val Rectangle.volume; get() = this.width * this.height

operator fun Rectangle2D.component1() = this.x
operator fun Rectangle2D.component2() = this.y
operator fun Rectangle2D.component3() = this.width
operator fun Rectangle2D.component4() = this.height

fun Rectangle.isAdjacentTo(other: Rectangle): Boolean {
    val (x1, y1, w1, h1) = this
    val (x2, y2, w2, h2) = other
    return (x1 == x2 && w1 == w2 && (y1 + h1 == y2 || y1 == y2 + h2)) || (y1 == y2 && h1 == h2 && (x1 + w1 == x2 || x1 == x2 + w2))
}

/**
 * Adds [this] rectangle to the given [rectangles] while trying to minimize overlapping
 * areas and combining adjacent ones.
 */
fun Rectangle.combineWith(rectangles: MutableList<Rectangle>) {
    if (this.isEmpty) return

    var rectToUpdate: Rectangle? = null

    for (other in rectangles) {
        // If this rectangle already exists, ignore the current section.
        if (this == other) return

        // If their union volume is smaller than (or equal to) their volumes combined, replace it with their union.
        val union = this.union(other)
        if (union.volume <= this.volume + other.volume) {
            other.bounds = union
            rectToUpdate = other
            break
        }
    }

    if (rectToUpdate != null) {
        rectangles.remove(rectToUpdate)
        rectToUpdate.combineWith(rectangles)
        return
    }

    rectangles.add(this)
}
