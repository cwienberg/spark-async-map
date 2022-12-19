package net.gonzberg.spark.async.util

import java.util.concurrent.{
  ArrayBlockingQueue,
  BlockingQueue,
  ExecutorService,
  ThreadPoolExecutor,
  TimeUnit
}
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

private[async] object BatchAsyncMapIterator {

  val DEFAULT_BUFFER_SIZE: Int = 1
  val KEEP_ALIVE_TIME: Long = 15
  val KEEP_ALIVE_TIME_UNIT: TimeUnit = TimeUnit.SECONDS

  private def createThreadPoolExecutionContext(
    corePoolSize: Int,
    maxPoolSize: Int,
    bufferSize: Int
  ): ExecutionContext = {
    val workQueue: BlockingQueue[Runnable] = new ArrayBlockingQueue(bufferSize)
    val threadPoolExecutor: ExecutorService = new ThreadPoolExecutor(
      corePoolSize,
      maxPoolSize,
      KEEP_ALIVE_TIME,
      KEEP_ALIVE_TIME_UNIT,
      workQueue
    )
    ExecutionContext.fromExecutorService(threadPoolExecutor)
  }

  def apply[A, B](
    input: Iterator[A],
    op: A => B,
    bufferSize: Int = DEFAULT_BUFFER_SIZE
  ): Iterator[B] = {
    implicit val executionContext = createThreadPoolExecutionContext(
      Math.max(1, bufferSize / 2),
      bufferSize,
      bufferSize
    )
    BatchAsyncMapIterator.delegate(
      input,
      (a: A) => Future { op(a) },
      bufferSize
    )
  }

  def delegate[A, B](
    input: Iterator[A],
    op: A => Future[B],
    bufferSize: Int = DEFAULT_BUFFER_SIZE
  ): Iterator[B] = {
    new BatchAsyncMapIterator[A, B](input, op, bufferSize)
  }
}

private[async] class BatchAsyncMapIterator[A, B](
  input: Iterator[A],
  op: A => Future[B],
  bufferSize: Int = BatchAsyncMapIterator.DEFAULT_BUFFER_SIZE
) extends Iterator[B] {

  private[this] val buffer: mutable.Queue[Future[B]] =
    new mutable.Queue(bufferSize)
  while (input.hasNext && (buffer.size < bufferSize - 1)) {
    shiftToBuffer()
  }

  private[this] def shiftToBuffer(): Unit = {
    if (input.hasNext) {
      buffer.enqueue(op(input.next()))
    }
  }

  override def hasNext: Boolean = {
    buffer.nonEmpty || input.hasNext
  }

  override def next(): B = {
    require(hasNext, "Called next on empty iterator")
    shiftToBuffer()
    Await.result(buffer.dequeue(), Duration.Inf)
  }
}
