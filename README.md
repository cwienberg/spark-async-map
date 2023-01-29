# Spark Async Map

[![build status](https://github.com/cwienberg/spark-async-map/actions/workflows/release.yml/badge.svg)](https://github.com/cwienberg/spark-async-map/actions/workflows/release.yml) [![codecov](https://codecov.io/gh/cwienberg/spark-async-map/branch/main/graph/badge.svg?token=IC5NUTYXHI)](https://codecov.io/gh/cwienberg/spark-async-map) [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/r/https/s01.oss.sonatype.org/net.gonzberg/spark-async-map_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/releases/net/gonzberg/spark-async-map_2.13/) [![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/net.gonzberg/spark-async-map_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/net/gonzberg/spark-async-map_2.13/)

Spark Async Map is a library of convenience methods for running map operations in a less-blocking way. It is targeted to situations where you need to make an IO or similar call (e.g. contact an external service). While the author encourages you to re-architect your approach to have any data, models, etc you need present locally in your spark executors (with libraries, distributed cache, etc), in situations where you cannot this library is a resource. It is designed to have each executor core spin up a threadpool to make calls, reducing the periods where executor cores are idling waiting for a response.

## Usage
This library uses the extension methods pattern to add methods to RDDs or Datasets. You can import the implicits with:
```scala
import net.gonzberg.spark.async.implicits._
```

You can then call additional functions on certain RDDs or Datasets, e.g.
```scala
val ds: Dataset[Integer] = ?
val dsTimesThree = ds.asyncMap(_ * 3, batchSize = 5)
```

The batchSize parameter is present in all extension methods. It is optional and defaults to 1. Batch size controls the size of the threadpool in each executor core. Note that this parameter increases the parallelism of your job to the number of executor cores in your job times the batch size. Keep this in mind when you consider the impact of your Spark job on any service it communicates with.

## Supported Versions
This library attempts to support Scala `2.11`, `2.12`, and `2.13`. Since there is not a single version of Spark which supports all three of those Scala versions, this library is built against different versions of Spark depending on the Scala version.

| Scala | Spark |
| ----- | ----- |
| 2.11  | 2.4.8 |
| 2.12  | 3.3.1 |
| 2.13  | 3.3.1 |

Other combinations of versions may also work, but these are the ones for which the tests run automatically. We will likely drop `2.11` support in a later release, depending on when it becomes too difficult to support.

## Documentation

Scaladocs are avaiable [here](https://cwienberg.github.io/spark-async-map/).

## Development

This package is built using `sbt`. You can run the tests with `sbt test`. You can lint with `sbt scalafmt`. You can use `+` in front of a directive to cross-build, though you'll need Java 8 (as opposed to Java 11) to cross-build to Scala 2.11. 

