package net.gonzberg.spark.async

import net.gonzberg.spark.async.util.BatchAsyncMapIterator
import org.apache.spark.rdd.RDD

import scala.language.implicitConversions
import scala.reflect.ClassTag

private[async] object AsyncMapRDD {
  implicit def rddToAsyncMapRDD[A](rdd: RDD[A]): AsyncMapRDD[A] = {
    new AsyncMapRDD(rdd)
  }
}

final class AsyncMapRDD[A](rdd: RDD[A]) extends Serializable {
  implicit def asyncMap[B: ClassTag](op: A => B, batchSize: Int = 1): RDD[B] = {
    rdd.mapPartitions { partition =>
      BatchAsyncMapIterator.apply(partition, op, batchSize)
    }
  }
}
