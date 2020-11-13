package dev.volix.rewinside.odyssey.common.frames.alignment

/**
 * @author Benedikt Wüller
 */
enum class Alignment(val vertical: VerticalAlignment, val horizontal: HorizontalAlignment) {

    TOP_LEFT(VerticalAlignment.TOP, HorizontalAlignment.LEFT),
    TOP_CENTER(VerticalAlignment.TOP, HorizontalAlignment.CENTER),
    TOP_RIGHT(VerticalAlignment.TOP, HorizontalAlignment.RIGHT),

    CENTER_LEFT(VerticalAlignment.CENTER, HorizontalAlignment.LEFT),
    CENTER_CENTER(VerticalAlignment.CENTER, HorizontalAlignment.CENTER),
    CENTER_RIGHT(VerticalAlignment.CENTER, HorizontalAlignment.RIGHT),

    BOTTOM_LEFT(VerticalAlignment.BOTTOM, HorizontalAlignment.LEFT),
    BOTTOM_CENTER(VerticalAlignment.BOTTOM, HorizontalAlignment.CENTER),
    BOTTOM_RIGHT(VerticalAlignment.BOTTOM, HorizontalAlignment.RIGHT)

}
