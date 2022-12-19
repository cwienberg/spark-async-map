package net.gonzberg.spark.async

import AsyncMapDataset.datasetToAsyncMapDataset
import org.apache.spark.sql.Dataset
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AsyncMapDatasetTest extends AnyFunSuite with Matchers with SparkTestingMixin {

  import spark.implicits._

  test("testAsyncMapEmpty") {
    val input: Dataset[Int] = spark.emptyDataset[Int]
    val actual = input.asyncMap(_ * 3, 5).collect()
    assert(actual.isEmpty)
  }

  test("testAsyncMapFull") {
    val input = 1.to(10000).toVector
    val inputDataset: Dataset[Int] = input.toDS()
    val expected = input.map(_*3)
    val actual = inputDataset.asyncMap(_ * 3, 5).collect()
    expected should contain theSameElementsAs actual
  }

}
