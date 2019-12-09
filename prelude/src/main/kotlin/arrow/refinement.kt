package arrow

import arrow.TypeProof.*

inline class PositiveInt(val value: Int)

@Proof(Subtyping)
fun Int.toPositiveInt(): PositiveInt? =
  if (this >= 0) PositiveInt(this) else null

@Proof(Subtyping)
fun PositiveInt.toPositiveInt(): Int =
  value

@Proof(Subtyping)
fun PositiveInt?.toPositiveInt(): Int? =
  this?.value