package io.pipekit.oss.spark.argo

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

import java.util.concurrent.TimeUnit

object BikeRideLengthJob {

  val fileLocation = "/app/df_1_year.csv"


  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("BikeRideLengthJob")
      .getOrCreate()

    val rdd = spark.sparkContext.textFile(fileLocation).map(Bike.toBikeRide)

    val bikeTypeStats = rdd
      .map(x => (x.bikeType, x.rideLength))
      .reduceByKey((x, y) => x + y)
      .sortBy(x => x._2, ascending = false).collect()

    for (elem <- bikeTypeStats) {
      println(s"BikeType ${elem._1}, rideLength in hours ${TimeUnit.MILLISECONDS.toHours(elem._2)}")
    }

    spark.stop()
  }


}
