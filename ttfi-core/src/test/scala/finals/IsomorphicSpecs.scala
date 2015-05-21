package finals

import org.specs2.Specification
import initial.FP.{
  Exp => Initial,
  Lit,
  Neg,
  Add,
  norm,
  view => initialView
}

class IsomorphicSpecs extends Specification {
  def is = s2"""
  final => initial        $e1
  initial => final        $e2
  view tf3                $e3
  check norm of ti3       $e4
  iso norm of tf3         $e5
"""

  import Exp.view
  import ExpSym.ExpSym

  def tf1[repr[_]](implicit s: ExpSym[repr]): repr[Integer] = {
    import s._
    add(
      lit(8))(
        neg(
          add(
            lit(1))(
              lit(2))))
  }

  def tf3[repr[_]](implicit s: ExpSym[repr]): repr[Integer] = {
    import s._
    add(tf1(s))(neg(neg(tf1(s))))
  }

  import Isomorphic._

  val ti1 = initialize(tf1) // same as tf1[Initial]
  val ti3 = initialize(tf3) // same as tf3[Initial]

  def e1 = {
    ti1 === Add(Lit(8), Neg(Add(Lit(1), Lit(2)))) &&
      ti3 === Add(ti1, Neg(Neg(ti1)))
  }

  def tif1[repr[_]: ExpSym]: repr[Integer] = Isomorphic.finalize[repr](ti1)
  def tif3[repr[_]: ExpSym]: repr[Integer] = Isomorphic.finalize[repr](ti3)

  def e2 = {
    view(tif1[Exp.Debug]) === initialView(ti1) &&
      view(tif3[Exp.Debug]) === initialView(ti3)
  }

  def e3 = view(tf3[Exp.Debug]) === "((8 + (-(1 + 2))) + (-(-(8 + (-(1 + 2))))))"

  def e4 = {
    initialView(norm(ti3)) === "(8 + ((-1) + ((-2) + (8 + ((-1) + (-2))))))"
  }

  def e5 = {
    view(
      Isomorphic.finalize[Exp.Debug](
        norm(
          initialize(
            tf3)))) === initialView(norm(ti3))
  }
}