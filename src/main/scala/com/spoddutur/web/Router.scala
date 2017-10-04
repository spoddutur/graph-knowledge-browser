package com.spoddutur.web

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import com.spoddutur.web.WebServer.{complete, get, getFromResource, getFromResourceDirectory, parameter, parameters, path, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Directives._
import com.spoddutur.CountryApiService
import com.spoddutur.util.SparkFactory

/**
  * Created by surthi on 02/08/17.
  */
trait Router {
  def route: Route
}

object IndexRouter extends Router {

  def route = path("index") {
    getFromResource("web/index.html")
  } ~ getFromResourceDirectory("web")
}

object HomePageRouter extends Router {
  def route = pathEndOrSingleSlash {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Following routes are configured in this app:</h1> <ul><li>/country</li><li>/version</li></ul>"))
    }
  }
}

object JsonCountryRouter extends Router with JsonSupport {

  def route = path("country") {
    parameter('query.as[String]) { query =>
      complete(CountryApiService.getCountryInfo(query))
    }
  }
}

/*object StringCompanyRouter extends Router with JsonSupport {

  def route = path("testComp") {
    parameter('id.as[Long]) { id =>
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>TestCompany Edges:: ${CompanyApiService.testComp(id)}"))
    }
  }
}*/

object SampleRouter extends Router {

  def route = path("customer"/IntNumber) { id =>
    complete {
      s"CustId: ${id}"
    }
  } ~
    path("customer") {
      parameter('id.as[Int]) { id =>
        complete {
          s"CustId: ${id}"
        }
      }
    } ~
    path("color") {
      parameters('r.as[Int], 'g.as[Int], 'b.as[Int]) { (r1, g, b) =>

        complete {
          s"(R,G,B): ${r1}, ${g}, ${b}"
        }
      }
    }
}

object SparkVersionCheckRouter extends Router {
  def route =  path("version") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Spark version: ${SparkFactory.sc.version}</h1>"))
    }
  }
}