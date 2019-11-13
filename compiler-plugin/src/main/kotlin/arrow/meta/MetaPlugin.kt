package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.higherkind.typeProofs
import kotlin.contracts.ExperimentalContracts

/**
 * The Meta Plugin contains the default meta bundled plugins for Arrow
 *
 * Compiler Plugin Authors can create a similar class or override this one to
 * provide their plugins.
 */
open class MetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> =
    listOf(
      //unionTypes,
      //higherKindedTypes,
      typeProofs
      //typeClasses,
      //comprehensions,
      //lenses
    )
}


