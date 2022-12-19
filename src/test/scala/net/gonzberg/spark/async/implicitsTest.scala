package net.gonzberg.spark.async

import implicits._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class implicitsTest extends AnyFunSuite with Matchers with SparkTestingMixin {

  import spark.implicits._

  test("testDatasetToAsyncMapDataset implicits work") {
    val input = 1.to(10000).toVector
    val inputDataset: Dataset[Int] = input.toDS()
    val expected = input.map(_*3)
    val actual = inputDataset.asyncMap(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

  test("testRddToAsyncMapRDD implicits work") {
    val input = 1.to(10000).toVector
    val inputRDD: RDD[Int] = sc.parallelize(input, 10)
    val expected = input.map(_*3)
    val actual = inputRDD.asyncMap(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

  test("testRddToAsyncMapValuesPairRDD implicits work") {
    val input: Seq[(Int, Int)] = 1.to(10000).map(i => i -> i)
    val inputRDD: RDD[(Int, Int)] = sc.parallelize(input, 10)
    val expected = input.map {case (k, v) => k -> v * 3}
    val actual = inputRDD.asyncMapValues(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

}
