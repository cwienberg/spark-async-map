package net.gonzberg.spark.async

import AsyncMapValuesPairRDD.rddToAsyncMapValuesPairRDD
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AsyncMapValuesPairRDDTest extends AnyFunSuite with Matchers with SparkTestingMixin {

  test("testAsyncMapEmpty") {
    val input: RDD[(Int, Int)] = sc.emptyRDD
    val actual = input.asyncMapValues(_ * 3, 5).collect()
    assert(actual.isEmpty)
  }

  test("testAsyncMapFull") {
    val input: Seq[(Int, Int)] = 1.to(10000).map(i => i -> i)
    val inputRDD: RDD[(Int, Int)] = sc.parallelize(input, 10)
    val expected = input.map {case (k, v) => k -> v * 3}
    val actual = inputRDD.asyncMapValues(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

}