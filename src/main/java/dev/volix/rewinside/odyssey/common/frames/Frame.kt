package dev.volix.rewinside.odyssey.common.frames

import dev.volix.rewinside.odyssey.common.frames.component.Component
import dev.volix.rewinside.odyssey.common.frames.helper.combineWith
import dev.volix.rewinside.odyssey.common.frames.helper.div
import dev.volix.rewinside.odyssey.common.frames.color.ColorTransformer
import dev.volix.rewinside.odyssey.common.frames.color.DefaultColorTransformer
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.util.Comparator
import kotlin.math.min

/**
 * @author Benedikt Wüller
 */
abstract class Frame(protected val canvasDimensions: Dimension, val viewportDimension: Dimension = canvasDimensions,
                     initialUpdateInterval: Long = 0L, val transformer: ColorTransformer = DefaultColorTransformer()) {

    private val viewport = BufferedImage(this.viewportDimension.width, this.viewportDimension.height, BufferedImage.TYPE_INT_ARGB)
    private val viewportScale = this.viewportDimension / this.canvasDimensions

    private var previousComponent: Component? = null
    private var startedAt: Long = System.currentTimeMillis()
    private var lastUpdateAt: Long = this.startedAt

    protected var totalTime: Long = 0; private set

    private val updatedSections = mutableListOf<Rectangle>()

    protected var updateInterval = initialUpdateInterval

    private var pausedAt: Long = 0

    var isPaused = false; private set
    private var isPausing = false

    /**
     * Executes a frame tick.
     *
     * If the delta time since the last update is equal or greater than [updateInterval],
     * an [update] call will be triggered. An update can be forced by passing [forceUpdate],
     * even if the delta time is too small.
     *
     * If an update has been executed or if the return value of [getRenderComponent] has changed,
     * a [render] call will be triggered, resulting in a partial or full re-render of the viewport.
     *
     * Use [hasUpdatedSections] to determine, whether a part or all of the [viewport] has been re-rendered.
     * If so, the updated sections can be obtained using [pullUpdatedSections].
     */
    fun tick(forceUpdate: Boolean = false) {
        if (this.isPausing && this.isPaused && !forceUpdate) return
        this.isPausing = this.isPaused

        val currentTime = System.currentTimeMillis()
        val delta = currentTime - this.lastUpdateAt

        this.totalTime = currentTime - this.startedAt

        this.onTick(this.totalTime, delta)

        // Determine whether an update is due.
        var updated = false
        if (forceUpdate || (this.updateInterval in 1..delta)) {
            updated = this.update(this.totalTime, delta)
            this.lastUpdateAt = currentTime
        }

        val component = this.getRenderComponent()
        val fullRender = component != this.previousComponent

        // Determine whether a render cycle should be triggered.
        if (!updated && !component.dirty && !fullRender) return
        this.render(component, fullRender)
        this.previousComponent = component
    }

    /**
     * @return whether any section has been re-rendered since the last call of [pullUpdatedSections].
     */
    fun hasUpdatedSections() = this.updatedSections.isNotEmpty()

    /**
     * @return the current viewport.
     */
    fun getViewport() = this.viewport

    /**
     * @return all sections that have been re-rendered since the last method call.
     */
    fun pullUpdatedSections(): List<Rectangle> {
        val sections = this.updatedSections.toList()
        this.clearUpdatedSections()
        return sections
    }

    fun clearUpdatedSections() = this.updatedSections.clear()

    /**
     * Calls [onUpdate] as many times as the [updateInterval] fits in [delta].
     * [onUpdate] will always be called at least once.
     *
     * @param[currentTime] the time in milliseconds since the last reset.
     * @param[delta] the time in milliseconds that passed since the last update.
     */
    protected open fun update(currentTime: Long, delta: Long): Boolean {
        val updates = min(1, delta / this.updateInterval).toInt()

        var updated = false
        repeat(updates) {
            updated = this.onUpdate(currentTime, delta / updates) || updated
        }
        return updated
    }

    /**
     * Renders the current state to the viewport. Only components which have been marked
     * as dirty will be re-drawn. Re-rendered sections will be added to [updatedSections].
     */
    private fun render(component: Component, fullRender: Boolean) {
        val context = this.viewport.graphics as Graphics2D
        context.scale(this.viewportScale.getWidth(), this.viewportScale.getHeight())

        this.calculateUpdatedSections(component, fullRender)
        this.updatedSections.forEach { component.render(context, it / this.viewportScale) }
        this.updatedSections.forEach { this.transformer.convert(this.viewport, it) }
    }

    /**
     * Determines, combines and minimizes all sections to be re-rendered and combines
     * them with the [updatedSections].
     */
    private fun calculateUpdatedSections(component: Component, fullRender: Boolean) {
        if (fullRender) {
            component.resetPreviousBounds()
            component.dirty = false
            this.updatedSections.clear()
            this.updatedSections.add(Rectangle(0, 0, this.viewportDimension.width, this.viewportDimension.height))
            return
        }

        component.calculateSectionsToRender()
                .sortedWith(Comparator.comparingInt { a: Rectangle -> a.x }.thenComparingInt { a: Rectangle -> a.y })
                .forEach { rect ->
                    rect.x = (rect.x * this.viewportScale.getWidth()).toInt()
                    rect.y = (rect.y * this.viewportScale.getHeight()).toInt()
                    rect.width = (rect.width * this.viewportScale.getWidth()).toInt()
                    rect.height = (rect.height * this.viewportScale.getHeight()).toInt()
                    rect.combineWith(this.updatedSections)
                }
    }

    /**
     * Pauses update and render cycles after the next tick.
     * Calls [onPause].
     */
    fun pause() {
        if (this.isPaused) return
        this.isPaused = true
        this.pausedAt = System.currentTimeMillis()
        this.onPause()
    }

    /**
     * Resumes the
     * Calls [onResume].
     */
    fun resume() {
        if (!this.isPaused) return

        val difference = System.currentTimeMillis() - this.pausedAt
        this.lastUpdateAt += difference
        this.startedAt += difference
        this.pausedAt = 0

        this.isPaused = false
        this.onResume()
    }

    protected open fun onPause() = Unit

    protected open fun onResume() = Unit

    /**
     * Called every time the [tick] method is called.
     *
     * @param[currentTime] the time in milliseconds since the last reset.
     * @param[delta] the time in milliseconds that passed since the last update.
     */
    protected abstract fun onTick(currentTime: Long, delta: Long)

    /**
     * Called when an update is due.
     *
     * @param[currentTime] the time in milliseconds since the last reset.
     * @param[delta] the time in milliseconds that passed since the last update.
     *
     * @return whether data has changed and the render cycle should be triggered.
     */
    protected abstract fun onUpdate(currentTime: Long, delta: Long): Boolean

    /**
     * Returns the base component to render to the canvas.
     * If the component differs from the previous one, a full frame render will
     * be triggered the next time.
     *
     * @return the base component to render.
     */
    protected abstract fun getRenderComponent(): Component

}
