package net.gonzberg.spark.async

import net.gonzberg.spark.async.util.BatchAsyncMapIterator
import org.apache.spark.sql.{Dataset, Encoder}

import scala.language.implicitConversions

private[async] object AsyncMapDataset {
  implicit def datasetToAsyncMapDataset[A](
    dataset: Dataset[A]
  ): AsyncMapDataset[A] = {
    new AsyncMapDataset(dataset)
  }
}

final class AsyncMapDataset[A](dataset: Dataset[A]) extends Serializable {

  /** Runs map over the Dataset in a threadpool, e.g. to parallelize blocking IO.
    * @param op The function to run
    * @param batchSize The size of the threadpool (i.e. the amount of parallel execution within each executor core)
    * @tparam B The result type of the map operation
    * @return A new Dataset, having applied op to all elements of this Dataset
    */
  implicit def asyncMap[B](op: A => B, batchSize: Int = 1)(implicit
    evidence: Encoder[B]
  ): Dataset[B] = {
    dataset.mapPartitions { partition =>
      BatchAsyncMapIterator.apply(partition, op, batchSize)
    }
  }
}
