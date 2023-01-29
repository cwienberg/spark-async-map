package net.gonzberg.spark.async

import net.gonzberg.spark.async.util.BatchAsyncMapIterator
import org.apache.spark.rdd.RDD

import scala.language.implicitConversions
import scala.reflect.ClassTag

private[async] object AsyncMapValuesPairRDD {
  implicit def rddToAsyncMapValuesPairRDD[K, V](
    rdd: RDD[(K, V)]
  ): AsyncMapValuesPairRDD[K, V] = {
    new AsyncMapValuesPairRDD(rdd)
  }
}

final class AsyncMapValuesPairRDD[K, V](rdd: RDD[(K, V)]) extends Serializable {

  /** Runs map over the RDD's values in a threadpool, e.g. to parallelize blocking IO.
    * @param op The function to run
    * @param batchSize The size of the threadpool (i.e. the amount of parallel execution within each executor core)
    * @tparam B The result type of the map operation
    * @return A new RDD of key-value pairs, having applied op to all values of this RDD
    */
  implicit def asyncMapValues[U: ClassTag](
    op: V => U,
    batchSize: Int = 1
  ): RDD[(K, U)] = {
    rdd.mapPartitions(
      { partition =>
        BatchAsyncMapIterator.apply(
          partition,
          (kv: (K, V)) => kv match { case (k, v) => (k, op(v)) },
          batchSize
        )
      },
      true
    )
  }
}
