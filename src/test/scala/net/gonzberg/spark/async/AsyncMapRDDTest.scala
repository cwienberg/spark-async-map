package net.gonzberg.spark.async

import AsyncMapRDD.rddToAsyncMapRDD
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AsyncMapRDDTest extends AnyFunSuite with Matchers with SparkTestingMixin {

  test("testAsyncMapEmpty") {
    val input: RDD[Int] = sc.emptyRDD
    val actual = input.asyncMap(_ * 3, 5).collect()
    assert(actual.isEmpty)
  }

  test("testAsyncMapFull") {
    val input = 1.to(10000).toVector
    val inputRDD: RDD[Int] = sc.parallelize(input, 10)
    val expected = input.map(_*3)
    val actual = inputRDD.asyncMap(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

}
