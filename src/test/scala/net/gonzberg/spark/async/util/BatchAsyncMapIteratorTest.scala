package net.gonzberg.spark.async.util

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

class BatchAsyncMapIteratorTest extends AnyFunSuite with Matchers {

  val BUFFER_SIZE: Int = 5
  val FULL_INPUT: Vector[Int] = 1.to(100).toVector
  val EXPECTED_FULL_OUTPUT: Array[Int] = FULL_INPUT.map(_*3).toArray

  implicit val testExecutionContext: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  test("testApplyEmpty") {
    val actualOutput = BatchAsyncMapIterator.apply[Int,Int](Iterator.empty, (x: Int) => x * 3, BUFFER_SIZE).toVector
    assert(actualOutput.isEmpty)
  }

  test("testApplyFull") {
    val actualOutput = BatchAsyncMapIterator.apply(FULL_INPUT.iterator, (x: Int) => x * 3, BUFFER_SIZE).toVector
    EXPECTED_FULL_OUTPUT should contain theSameElementsInOrderAs actualOutput
  }

  test("testDelegateEmpty") {
    val actualOutput = BatchAsyncMapIterator.delegate[Int,Int](Iterator.empty, (x: Int) => Future{x * 3}, BUFFER_SIZE).toVector
    assert(actualOutput.isEmpty)
  }

  test("testDelegateFull") {
    val actualOutput = BatchAsyncMapIterator.delegate(FULL_INPUT.iterator, (x: Int) => Future{x * 3}, BUFFER_SIZE).toVector
    EXPECTED_FULL_OUTPUT should contain theSameElementsInOrderAs actualOutput
  }

}
