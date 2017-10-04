package com.spoddutur

import com.spoddutur.util.SparkFactory
import org.apache.spark.sql.Encoders

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.immutable

/**
  * Created by sruthi on 02/08/17.
  * Service class computing the value for route bindings "/activeStreams" and "/count" respectively.
  */

case class Edge(source: Long, target: Long, relation: String) {}
case class Node(name: String, group: Int) {
  override def equals(that: scala.Any): Boolean = {
    that match {
      case that: Node => {
        that.name.equals(this.name)
      }
      case _ => false
    }
  }
}

case class CountryGraph(nodes: Seq[Node], edges: Seq[Edge]) {}

object CountryApiService {

  val sc = SparkFactory.sc

  /*
   1. Queries spark and gets the triplet (CountryCode, SeriesCode, Value)
   2. Maps the results as CountryGraph and returns it.

   Example row: ["IND", "SP.DYN.MORT", "50"] depicts india's mortality rate
   For each datum, add 2 nodes (src, target) and one edge (for the relation between them)
   Example: ["IND", "SP.DYN.MORT", "50"] will translate to =>
   1. SourceNode: Node("IND"),
   2. TargetNode: Node("50"),
   3. Relation between them: Edge(1,2,"SP.DYN.MORT")

   The final CountryGraph returned will encapsulate all such Nodes and Edges based on user query.
  */
  def getCountryInfo(query: String): CountryGraph = {

    import SparkFactory.spark.implicits._
    // val resultRow = SparkFactory.countries.where($"CountryCode" === "USA" || $"CountryCode" === "IND")
    // val resultRow = SparkFactory.countries.sample(false, 0.01)

    // Query spark
    val resultRow = SparkFactory.countries.where(query)
    val resultAsList = resultRow.map(row => {
      row.mkString(",")
    }).as(Encoders.STRING).collectAsList();

    // initialize nodes and edges as empty collection
    var edges = new mutable.HashSet[Edge]()
    var nodes = new ListBuffer[Node]()

    // 'group' is used to color code related nodes with same color in UI.
    var group = 0

    // For each datum, add 2 nodes (src, target) and one edge (for the relation between them)
    for(index <- 0 to resultAsList.size()-1) {

      var input = resultAsList.get(index)
      val inputTriplet = input.split(",")
      var countryCode = inputTriplet(0)
      val seriesCode = inputTriplet(1)
      val value = inputTriplet(2)

      // Check if the value is not null and valid. Only then, add it to graph.
      if(value != null && value != "null" && value != "..") {

        // 1. Check if countryCode is added as Node to graph already. If no, then append it.
        val indexOfCountry = nodes.indexOf(Node(countryCode, 1))
        if (indexOfCountry == -1) {
          // indexOfCountry = group // group = group + 1
          group = index
          nodes += Node(countryCode, group)
        }

        // 2. Append target node i.e., value
        val targetNode = Node(value, group)
        nodes += targetNode

        // 3. Append edge (srcNode, targetNode, their_relation)
        val sourceNodeIndex = nodes.indexOf(Node(countryCode, group))
        val targetNodeIndex = nodes.indexOf(targetNode) // nodes.length-1
        edges += Edge(sourceNodeIndex, targetNodeIndex, seriesCode)
      }
    }

    // Return graph
    CountryGraph(nodes.toSeq, edges.toSeq)
  }
}

