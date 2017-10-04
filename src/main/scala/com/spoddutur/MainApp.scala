package com.spoddutur

import akka.http.scaladsl.settings.ServerSettings
import com.spoddutur.util.{AppConfig, SparkFactory}
import com.spoddutur.web.WebServer
import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}

/**
  * Created by sruthi on 02/08/17.
  */
object MainApp extends App {

  var basicConfig = ConfigFactory.load("reference.conf")
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  // init config params from cmd-line args
  AppConfig.parse(this.args.toList)

  // initialise spark session
  val spark = SparkFactory.spark
  import spark.implicits._

  // sample query
  println(CountryApiService.getCountryInfo("CountryCode = 'IND'"))

  // Starting the server
  WebServer.startServer("localhost", AppConfig.akkaHttpPort, ServerSettings(basicConfig))
  println(s"Server online at http://localhost:", AppConfig.akkaHttpPort, "/")
  println("Done")
}
