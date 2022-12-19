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
