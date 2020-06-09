package arrow.meta.ide.plugins.quotes

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.quotes.highlighting.quoteHighlighting
import arrow.meta.ide.plugins.quotes.lifecycle.quoteLifecycle
import arrow.meta.ide.plugins.quotes.synthetic.quoteSyntheticPackageFragmentProvider
import arrow.meta.ide.plugins.quotes.synthetic.quoteSyntheticResolver
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import arrow.meta.ide.dsl.utils.ctx
import arrow.meta.phases.CompilerContext

/**
 * Please, view the sub directories `cache` , `synthetic`, `system`, `lifecycle` and `highlighting` for quotes related IDE features.
 * Enabling [quotes] implies enabling the quote-related service registration in the plugin.xml that is:
 * [arrow.meta.ide.plugins.quotes.cache.QuoteCacheService]
 * [arrow.meta.ide.plugins.quotes.system.QuoteSystem]
 * [arrow.meta.ide.plugins.quotes.highlighting.QuoteHighlightingCache]
 * Please, be advised not to remove the aforementioned services from the plugin.xml,
 * as this may lead to deadlocks in the following features down below.
 * However if one chooses to disable [quotes] or any other [IdePlugin], one may do so by removing it in the [IdeMetaPlugin.intercept] list.
 */
val IdeMetaPlugin.quotes: IdePlugin
  get() = "Quote Ide Plugin" {
    meta(
      quoteLifecycle,
      quoteHighlighting,
      quoteSyntheticResolver,
      quoteSyntheticPackageFragmentProvider
    )
  }


class ErrorAnnotator : Annotator {
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    element.ctx()?.let {
      println("HIIII I HAVE THE EXTENSION calling ctx()")
    }
    element.project.getService(CompilerContext::class.java)?.let {
      println("I HAVE THE EXTENSION with an explicit call")
    }
  }
}