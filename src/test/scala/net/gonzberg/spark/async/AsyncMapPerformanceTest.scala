package net.gonzberg.spark.async

import implicits._
import org.apache.spark.sql.Dataset
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AsyncMapPerformanceTest extends AnyFunSuite with Matchers with SparkTestingMixin {

  import spark.implicits._

  test("runSyncDatasetMap") {
    val input = 1.to(10000).toVector
    val inputDataset: Dataset[Int] = input.toDS()
    inputDataset.map { x =>
      Thread.sleep(10)
      x
    }.foreach(_ => ())
  }

  test("runAsyncDatasetMap") {
    val input = 1.to(10000).toVector
    val inputDataset: Dataset[Int] = input.toDS()
    inputDataset.asyncMap(
      { x =>
        Thread.sleep(10)
        x
      },
      20).foreach(_ => ())
  }

}
