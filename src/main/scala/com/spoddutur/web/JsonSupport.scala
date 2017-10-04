package com.spoddutur.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.spoddutur.{CountryGraph, Edge, Node}
import spray.json.DefaultJsonProtocol

/**
  * Created by sruthi on 02/08/17.
  */
// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val nodeFormat = jsonFormat2(Node)
  implicit val edgeFormat = jsonFormat3(Edge)
  implicit val countryFormat = jsonFormat2(CountryGraph)
}