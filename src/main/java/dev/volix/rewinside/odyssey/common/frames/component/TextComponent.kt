package dev.volix.rewinside.odyssey.common.frames.component

import dev.volix.rewinside.odyssey.common.frames.alignment.Alignment
import dev.volix.rewinside.odyssey.common.frames.alignment.HorizontalAlignment
import dev.volix.rewinside.odyssey.common.frames.alignment.VerticalAlignment
import dev.volix.rewinside.odyssey.common.frames.helper.component1
import dev.volix.rewinside.odyssey.common.frames.helper.component2
import dev.volix.rewinside.odyssey.common.frames.helper.component3
import dev.volix.rewinside.odyssey.common.frames.helper.component4
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage

/**
 * @author Benedikt Wüller
 */
open class TextComponent(position: Point, text: String?, color: Color,
                    font: Font, alignment: Alignment)
    : Component(position, Dimension()) {

    val basePosition = Point(position)

    var text = text; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        this.update()
    }

    var color = color; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        this.update()
    }

    var font = font; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        this.update()
    }

    var alignment = alignment; set(value) {
        this.dirty = this.dirty || field != value
        field = value
        this.update()
    }

    private var heightOffset = 0
    private var image: BufferedImage? = null

    init {
        this.update()
    }

    private fun update() {
        val renderContext = FontRenderContext(this.font.transform, true, true)
        val bounds = this.text?.let { this.font.getStringBounds(text, renderContext) } ?: Rectangle()

        val (_, _, width, height) = bounds

        this.dimensions.setSize(width, height)
        this.position.location = this.basePosition
        this.heightOffset = (this.dimensions.height * 0.2).toInt()
        this.dirty = true

        when (this.alignment.vertical) {
            VerticalAlignment.CENTER -> this.position.translate(0, -(this.dimensions.height / 2))
            VerticalAlignment.BOTTOM -> this.position.translate(0, -this.dimensions.height)
            else -> Unit
        }

        when (this.alignment.horizontal) {
            HorizontalAlignment.CENTER -> this.position.translate(-this.dimensions.width / 2, 0)
            HorizontalAlignment.RIGHT -> this.position.translate(-this.dimensions.width, 0)
            else -> Unit
        }

        // Workaround for wrong calculated text height.
        this.dimensions.height = (height + this.heightOffset).toInt()
        this.image = null
    }

    override fun onRender(context: Graphics2D, bounds: Rectangle) {
        if (this.image == null) {
            this.image = BufferedImage(this.dimensions.width, this.dimensions.height, BufferedImage.TYPE_INT_ARGB)
            val sectionContext = this.image!!.createGraphics()
            sectionContext.font = this.font
            sectionContext.color = this.color
            sectionContext.drawString(this.text ?: return, 0, this.dimensions.height - this.heightOffset)
        }

        val minX = bounds.minX.toInt()
        val minY = bounds.minY.toInt()
        val maxX = bounds.maxX.toInt()
        val maxY = bounds.maxY.toInt()

        context.drawImage(this.image!!, minX, minY, maxX, maxY, minX, minY, maxX, maxY, null)
    }

}
