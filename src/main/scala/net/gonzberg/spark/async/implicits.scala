package net.gonzberg.spark.async

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

import scala.language.implicitConversions

object implicits {
  implicit def datasetToAsyncMapDataset[A](dataset: Dataset[A]): AsyncMapDataset[A] = {
    AsyncMapDataset.datasetToAsyncMapDataset(dataset)
  }

  implicit def rddToAsyncMapRDD[A](rdd: RDD[A]): AsyncMapRDD[A] = {
    AsyncMapRDD.rddToAsyncMapRDD(rdd)
  }

  implicit def rddToAsyncMapValuesPairRDD[K, V](rdd: RDD[(K, V)]): AsyncMapValuesPairRDD[K, V] = {
    AsyncMapValuesPairRDD.rddToAsyncMapValuesPairRDD(rdd)
  }
}
