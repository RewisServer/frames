package dev.volix.rewinside.odyssey.common.frames.resource.font

import java.awt.Font
import java.io.File

/**
 * Retrieves TrueType fonts from files relative to the [path].
 *
 * @author Benedikt Wüller
*/
class FontFileAdapter(private val path: String) : FontAdapter {

    private val baseFonts = mutableMapOf<String, Font>()

    override fun get(name: String, size: Float): Font {
        return this.baseFonts.getOrPut(name) {
            val path = this.path + File.separator + "$name.ttf"
            return@getOrPut Font.createFont(Font.TRUETYPE_FONT, File(path))
        }.deriveFont(size)
    }

}
