package arrow.meta.plugins.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.Test


class UnionTest {

  @Test
  fun `Union uber constructor remains visible`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = Union(0)
           | 
           |""".source
      },
      assert = {
        allOf("f().value".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.*
           |
           |fun f(): Union2<String, Int> = 0
           |val x: Int? = f()
           |""".source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |val x: Int? = f()
           |""".source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |val x: Int? = f()
           """.source
      },
      assert = {
        allOf("x".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union accepts typed values in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = "a"
           |
           |""".source
      },
      assert = {
        allOf("f().value".source.evalsTo("a"))
      }
    ))
  }

  @Test
  fun `Union rejects typed values not in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0.0
           |
           |""".source
      },
      assert = {
        allOf(failsWith { it.contains("The floating-point literal does not conform to the expected type Union2<String, Int>") })
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = "a"
           |fun z(): String? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo("a"))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |fun z(): Int? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types also present in the union 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |fun z(): Int? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 2`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun z(): String? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 3`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union3
           |fun f(): Union3<String, Int, Double> = 0
           |fun z(): String? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union converts to nullable type with null when the union value is absent 4`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union4
           |fun f(): Union4<String, Int, Double, List<Int>> = 0
           |fun z(): String? = f()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(null))
      }
    ))
  }

  @Test
  fun `Union can convert to nullable types letting inference pass through`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun x() = f()
           |fun z(): Int? = x()
           |""".source
      },
      assert = {
        allOf("z()".source.evalsTo(0))
      }
    ))
  }

  @Test
  fun `Union fails to convert nullable types not present in the union`() {
    assertThis(CompilerTest(
      config = { metaDependencies },
      code = {
        """|import arrow.Union2
           |fun f(): Union2<String, Int> = 0
           |fun z(): Double? = f()
           |""".source
      },
      assert = {
        allOf(failsWith { it.contains("Type mismatch: inferred type is Union2<String, Int> but Double? was expected") })
      }
    ))
  }

}
