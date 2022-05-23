/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats.tests

import cats.{Align, Applicative, Apply, CoflatMap}
import cats.data.{Const, State, Validated}
import cats.instances.unit
import cats.kernel.Monoid
import cats.kernel.laws.discipline.{MonoidTests, SemigroupTests}
import cats.laws.discipline.arbitrary._
import cats.laws.discipline.{AlignTests, CoflatMapTests}
import cats.syntax.applicative._
import cats.syntax.eq._
import cats.syntax.functor._
import org.scalacheck.Prop._

class ApplicativeSuite extends CatsSuite {

  test("replicateA creates a List of 'n' copies of given Applicative 'fa'") {
    val A = Applicative[Option]
    val fa = A.pure(1)
    assert(fa.replicateA(5) === (Some(List(1, 1, 1, 1, 1))))
  }

  test("replicateA_ executes the Applicative action 'fa' 'n' times") {
    val A = Applicative[Option]
    val fa = A.pure(0)
    assert(fa.replicateA_(5) === Option(unit))

    val increment: State[Int, Int] = State { i => (i + 1, i) }

    assert(increment.replicateA_(5).runS(0).value === 5)
    assert(increment.replicateA_(5).run(0).value === ((5, ())))
    assert(increment.replicateA_(5).run(0).value === increment.replicateA(5).void.run(0).value)
  }

  test("whenA return given argument when cond is true") {
    forAll { (l: List[Int]) =>
      assert(l.whenA(true) === (List.fill(l.length)(())))
    }
  }

  test("whenA lift Unit to F when cond is false") {
    forAll { (l: List[Int]) =>
      assert(l.whenA(false) === (List(())))
    }
  }

  test("unlessA return given argument when cond is false") {
    forAll { (l: List[Int]) =>
      assert(l.unlessA(false) === (List.fill(l.length)(())))
    }
  }

  test("unlessA lift Unit to F when cond is true") {
    forAll { (l: List[Int]) =>
      assert(l.unlessA(true) === (List(())))
    }
  }

  {
    implicit val optionMonoid: Monoid[Option[Int]] = Applicative.monoid[Option, Int]
    checkAll("Applicative[Option].monoid", MonoidTests[Option[Int]](optionMonoid).monoid)
  }

  {
    val optionSemigroupFromApply = Apply.semigroup[Option, Int]
    checkAll("Apply[Option].semigroup", SemigroupTests[Option[Int]](optionSemigroupFromApply).semigroup)
  }

  {
    implicit val listwrapperApplicative: Applicative[ListWrapper] = ListWrapper.applicative
    implicit val listwrapperCoflatMap: CoflatMap[ListWrapper] = Applicative.coflatMap[ListWrapper]
    checkAll("Applicative[ListWrapper].coflatMap", CoflatMapTests[ListWrapper].coflatMap[String, String, String])

    implicit val validatedCoflatMap: CoflatMap[Validated[String, *]] = Applicative.coflatMap[Validated[String, *]]
    checkAll("Applicative[Validated].coflatMap", CoflatMapTests[Validated[String, *]].coflatMap[String, String, String])

    implicit val constCoflatMap: CoflatMap[Const[String, *]] = Applicative.coflatMap[Const[String, *]]
    checkAll("Applicative[Const].coflatMap", CoflatMapTests[Const[String, *]].coflatMap[String, String, String])

    implicit val listwrapperAlign: Align[ListWrapper] = Apply.align[ListWrapper]
    checkAll("Apply[ListWrapper].align", AlignTests[ListWrapper].align[Int, Int, Int, Int])

    implicit val validatedAlign: Align[Validated[String, *]] = Apply.align[Validated[String, *]]
    checkAll("Apply[Validated].align", AlignTests[Validated[String, *]].align[Int, Int, Int, Int])

    implicit val constAlign: Align[Const[String, *]] = Apply.align[Const[String, *]]
    checkAll("Apply[Const].align", AlignTests[Const[String, *]].align[Int, Int, Int, Int])
  }

}
