package com.spoddutur.util

import org.apache.spark.sql.types.{LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}
import akka.http.scaladsl.settings.ServerSettings
import com.spoddutur.util.AppConfig
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.catalyst.expressions.MonotonicallyIncreasingID
import org.apache.spark.sql.types.{LongType, StructField, StructType}
import org.apache.spark.sql.functions._
import org.graphframes.GraphFrame

/**
  * Created by sruthi on 02/08/17.
  * Creates one SparkSession which is shared and reused among multiple HttpRequests
  */
object SparkFactory {

  val spark: SparkSession = SparkSession.builder
                                    .master(AppConfig.sparkMaster)
                                    .appName(AppConfig.sparkAppName)
                                    .getOrCreate
  val sc = spark.sparkContext
  val sparkConf = sc.getConf
  import spark.implicits._

  println("loading data as graph..")

  val countries = loadCountries(spark)

  /**
    * Loads countries open source data as Dataframe with three columns: ["CountryCode", "SeriesCode", "Value"]
    */
  private def loadCountries(spark: SparkSession): DataFrame = {

    // load data
    val df = spark.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("mode", "DROPMALFORMED")
      .load("file:///Users/surthi/mygitrepo/graph-knowledge-browser/data/countriesProfile.csv")

    // rename columns
    var newDf = df
    for(col <- df.columns){
      val r1 = col.replaceAll("[\\W]|_", "")
      newDf = newDf.withColumnRenamed(col,r1)
    }

    // print schema
    newDf.printSchema()
    newDf.show()

    //  return dataframe of 3 columns ["CountryCode", "SeriesCode", "Value"]
    newDf.selectExpr("CountryCode", "SeriesCode", "2016YR2016 as Value")
  }
}