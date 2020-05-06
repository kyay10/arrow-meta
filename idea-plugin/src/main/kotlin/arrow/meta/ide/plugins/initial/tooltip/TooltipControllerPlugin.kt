package arrow.meta.ide.plugins.initial.tooltip

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.hint.LineTooltipRenderer
import com.intellij.codeInsight.hint.TooltipController
import com.intellij.codeInsight.hint.TooltipGroup
import com.intellij.codeInsight.hint.TooltipRenderer
import com.intellij.ide.IdeTooltipManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Comparing
import com.intellij.ui.HintHint
import com.intellij.ui.LightweightHint
import com.intellij.ui.awt.RelativePoint
import java.awt.Point
import java.awt.event.MouseEvent

val IdeMetaPlugin.toolTipController: ExtensionPhase
  get() = addAppService(TooltipController::class.java) {
    controller // hijack TooltipController and replace it with ours
  }

private val controller: TooltipController
  get() = object : TooltipController() {

    private var currentMetaTooltip: LightweightHint? = null
    private var currentMetaTooltipGroup: TooltipGroup? = null
    private var currentMetaTooltipRenderer: TooltipRenderer? = null

    override fun cancelTooltips() {
      super.cancelTooltips() // cancels any parent tooltips first, if any. (all the non arrow-meta ones)
      hideMetaTooltip()
    }

    private fun hideMetaTooltip() {
      currentMetaTooltip?.let { tooltip ->
        currentMetaTooltip = null
        currentMetaTooltipGroup = null
        tooltip.hide()
        IdeTooltipManager.getInstance().hide(null) // resets everything in the TooltipManager.
      }
    }

    override fun cancelTooltip(groupId: TooltipGroup, mouseEvent: MouseEvent?, forced: Boolean) {
      if (groupId == currentMetaTooltipGroup) {
        if (!forced && currentMetaTooltip != null && currentMetaTooltip!!.canControlAutoHide()) return
        cancelTooltips()
      } else {
        super.cancelTooltip(groupId, mouseEvent, forced)
      }
    }

    /**
     * Returns newly created hint, or already existing (for the same renderer)
     */
    override fun showTooltipByMouseMove(
      editor: Editor,
      point: RelativePoint,
      tooltipObject: TooltipRenderer?,
      alignToRight: Boolean,
      group: TooltipGroup,
      hintHint: HintHint): LightweightHint? {

      if (tooltipObject != null && tooltipObject.isArrowMetaTooltip()) {
        val currentTooltip = currentMetaTooltip
        if (currentTooltip == null || !currentTooltip.isVisible || !currentTooltip.isQueuedToShow()) {
          currentMetaTooltipRenderer = null
        }

        // Returns current tooltip if it's the same one and it's available.
        if (Comparing.equal(tooltipObject, currentMetaTooltipRenderer)) {
          IdeTooltipManager.getInstance().cancelAutoHide()
          return currentMetaTooltip
        } else {
          hideMetaTooltip()

          val p = point.getPointOn(editor.component.rootPane.layeredPane).point
          if (!hintHint.isAwtTooltip) {
            p.x += if (alignToRight) -10 else 10
          }

          val project = editor.project
          if (project != null && !project.isOpen) return null
          if (editor.contentComponent.isShowing) {
            return doShowMetaTooltip(editor, p, tooltipObject, alignToRight, group, hintHint)
          }

          return null
        }
      } else {
        return super.showTooltipByMouseMove(editor, point, tooltipObject, alignToRight, group, hintHint)
      }
    }

    override fun showTooltip(editor: Editor,
                             p: Point,
                             tooltipRenderer: TooltipRenderer,
                             alignToRight: Boolean,
                             group: TooltipGroup,
                             hintInfo: HintHint) {
      if (tooltipRenderer.isArrowMetaTooltip()) {
        doShowMetaTooltip(editor, p, tooltipRenderer, alignToRight, group, hintInfo)
      } else {
        super.showTooltip(editor, p, tooltipRenderer, alignToRight, group, hintInfo)
      }
    }

    private fun doShowMetaTooltip(
      editor: Editor,
      p: Point,
      tooltipRenderer: TooltipRenderer,
      alignToRight: Boolean,
      group: TooltipGroup,
      hintInfo: HintHint
    ): LightweightHint? {

      val currentTooltip = currentMetaTooltip
      if (currentTooltip == null || !currentTooltip.isVisible) {
        currentMetaTooltipRenderer = null
      }

      // If it's the same renderer we don't want to show it again, but keep it there.
      if (Comparing.equal(tooltipRenderer, currentMetaTooltipRenderer)) {
        IdeTooltipManager.getInstance().cancelAutoHide()
        return null
      } else {
        if (currentMetaTooltipGroup != null && group < currentMetaTooltipGroup) return null
        val point = Point(p)
        hideMetaTooltip()

        val renderer = MetaTooltipRenderer(tooltipRenderer.unsafeLineText(), arrayOf())

        val hint = renderer.show(editor, point, alignToRight, group, hintInfo)
        currentMetaTooltipGroup = group
        currentMetaTooltip = hint
        currentMetaTooltipRenderer = tooltipRenderer
        return hint
      }
    }

    override fun shouldSurvive(e: MouseEvent?): Boolean {
      return super.shouldSurvive(e) ||
        (currentMetaTooltip != null && currentMetaTooltip!!.canControlAutoHide())
    }

    override fun hide(lightweightHint: LightweightHint) {
      super.hide(lightweightHint)
      if (currentMetaTooltip != null && currentMetaTooltip == lightweightHint) {
        hideMetaTooltip()
      }
    }

    override fun resetCurrent() {
      super.resetCurrent()
      currentMetaTooltip = null
      currentMetaTooltipGroup = null
      currentMetaTooltipRenderer = null
    }
  }

private fun TooltipRenderer.isArrowMetaTooltip(): Boolean =
  this is LineTooltipRenderer && !this.text.isNullOrEmpty()

private fun TooltipRenderer.unsafeLineText(): String = (this as LineTooltipRenderer).text!!

private fun LightweightHint.isQueuedToShow() =
  IdeTooltipManager.getInstance().isQueuedToShow(this.currentIdeTooltip)