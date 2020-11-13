package dev.volix.rewinside.odyssey.common.frames.resource.image

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Retrieves images and SpriteSheets from files relative to the [path].
 *
 * @author Benedikt Wüller
*/
class ImageFileAdapter(private val path: String) : ImageAdapter {

    private val images = mutableMapOf<String, BufferedImage>()
    private val spriteSheets = mutableMapOf<Config, SpriteSheet>()

    private data class Config(val name: String, val width: Int, val height: Int)

    override fun get(name: String): BufferedImage {
        return this.images.getOrPut(name) {
            val path = this.path + File.separator + "$name.png"
            return ImageIO.read(File(path))
        }
    }

    override fun getSheet(name: String, spriteWidth: Int, spriteHeight: Int): SpriteSheet {
        val config = Config(name, spriteWidth, spriteHeight)
        return this.spriteSheets.getOrPut(config) {
            val image = this.get(name)
            return@getOrPut SpriteSheet(image, spriteWidth, spriteHeight)
        }
    }

}
