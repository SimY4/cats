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

package cats.data

import cats.data.Chain.{nil, one, Wrap}

private[data] trait ChainCompanionCompat {

  /**
   * Creates a Chain from the specified sequence.
   */
  def fromSeq[A](s: Seq[A]): Chain[A] = {
    val lc = s.lengthCompare(1)
    if (lc < 0) nil
    else if (lc > 0) Wrap(s)
    else one(s.head)
  }

  /**
   * Creates a Chain from the specified IterableOnce.
   */
  def fromIterableOnce[A](xs: IterableOnce[A]): Chain[A] = Chain.fromSeq(
    xs match {
      case s: Seq[A] => s // pay O(1) not O(N) cost
      case notSeq    => notSeq.iterator.to(Vector) // toSeq could return a LazyList, creating potential race conditions
    }
  )
}
