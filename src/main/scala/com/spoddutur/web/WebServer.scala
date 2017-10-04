package com.spoddutur.web

/**
  * Created by sruthi on 02/08/17.
  */
import akka.http.scaladsl.server.{HttpApp, Route}

object WebServer extends HttpApp with JsonSupport {
  override def routes: Route = IndexRouter.route ~
    HomePageRouter.route ~
    SparkVersionCheckRouter.route ~
    JsonCountryRouter.route
}
