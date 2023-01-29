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

  /** Runs map over the RDD in a threadpool, e.g. to parallelize blocking IO.
    * @param op The function to run
    * @param batchSize The size of the threadpool (i.e. the amount of parallel execution within each executor core)
    * @tparam B The result type of the map operation
    * @return A new RDD, having applied op to all elements of this RDD
    */
  implicit def asyncMap[B: ClassTag](op: A => B, batchSize: Int = 1): RDD[B] = {
    rdd.mapPartitions { partition =>
      BatchAsyncMapIterator.apply(partition, op, batchSize)
    }
  }
}
