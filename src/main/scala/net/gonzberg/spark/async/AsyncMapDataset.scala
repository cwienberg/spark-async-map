package net.gonzberg.spark.async

import net.gonzberg.spark.async.util.BatchAsyncMapIterator
import org.apache.spark.sql.{Dataset, Encoder}

import scala.language.implicitConversions

private[async] object AsyncMapDataset {
  implicit def datasetToAsyncMapDataset[A](dataset: Dataset[A]): AsyncMapDataset[A] = {
    new AsyncMapDataset(dataset)
  }
}

final class AsyncMapDataset[A](dataset: Dataset[A]) extends Serializable {
  implicit def asyncMap[B](op: A => B, batchSize: Int = 1)(implicit evidence: Encoder[B]): Dataset[B] = {
    dataset.mapPartitions { partition =>
      BatchAsyncMapIterator.apply(partition, op, batchSize)
    }
  }
}
