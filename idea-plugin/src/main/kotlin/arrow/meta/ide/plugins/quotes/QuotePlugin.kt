package arrow.meta.ide.plugins.quotes

import arrow.meta.CliPlugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.quotes.lifecycle.quoteLifecycle
import arrow.meta.ide.plugins.quotes.lifecycle.quoteProjectOpened
import arrow.meta.ide.plugins.quotes.resolve.quoteSyntheticPackageFragmentProvider
import com.intellij.openapi.startup.StartupActivity
import arrow.meta.invoke as cli

/**
 * Please, view the sub directories `cache` , `resolve` and `system` for quotes related IDE features.
 * Disabling [quotes] implies disabling the quote-related features in the plugin.xml that is:
 * [arrow.meta.ide.plugins.quotes.cache.QuoteCacheService]
 * [arrow.meta.ide.plugins.quotes.resolve.QuoteHighlightingCache]
 * [arrow.meta.ide.plugins.quotes.system.QuoteSystem]
 * [arrow.meta.ide.plugins.quotes.resolve.QuoteHighlightingPassFactory]
 * [arrow.meta.ide.plugins.quotes.resolve.QuoteSyntheticResolveExtension]
 * [quotesCli]
 */
val IdeMetaPlugin.quotes: IdePlugin
  get() = "Quote Ide Plugin" {
    meta(
      quoteLifecycle,
      addPostStartupActivity(
        StartupActivity.DumbAware {
          quoteProjectOpened(it)
        }
      )
    )
  }

/**
 * quotes cli integration with the Ide
 */
val IdeMetaPlugin.quotesCli: CliPlugin
  get() = "Quotes Cli Integration".cli {
    meta(
      quoteSyntheticPackageFragmentProvider
    )
  }