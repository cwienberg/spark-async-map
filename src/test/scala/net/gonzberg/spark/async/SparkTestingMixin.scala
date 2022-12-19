package net.gonzberg.spark.async

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext

private[async] trait SparkTestingMixin {
  val spark: SparkSession = SparkTestingMixin.spark
  def sc: SparkContext = spark.sparkContext
}

private[async] object SparkTestingMixin {
  lazy val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("SparkAsyncMapTestApplication")
    .config("spark.driver.bindAddress", "127.0.0.1")
    .config("spark.ui.enabled", "false")
    .config("spark.worker.cleanup.enabled", "true")
    .config("spark.default.parallelism", "5")
    .config("spark.sql.shuffle.partitions", "5")
    .getOrCreate()
}
