/**
 * Created by sruthi on 02/08/2017.
 */

/**
 * The main controller for the app.
 * Exposes the model to the template and renders graph for user query.
 */

var app = angular.module("app", [])
app.config(function($locationProvider){
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    })
});

app.controller("KnowledgeBrowserController", function($scope, $http, $location) {
    $scope.helloTo = {};
    console.log("in app.js -", $location.search.id)
    $scope.helloTo.title = " Knowledge Browser";

    $scope.queryForm = {query: ""};

     $scope.submitTheForm = function() {
        var query = $scope.queryForm.query
        console.log(query)
        $scope.getCountryInfo(query)
    }

     $scope.getCountryInfo = function(query) {
      console.log("inside getCountryInfo", query);
        $http.get('http://localhost:8002/country?query=' + query)
            .then(function(response) {
                console.log(response.data)
                $scope.countryInfo = response.data;
            });
    }
});

app.directive("graph", function($window) {
 return{
   restrict: "EA",
   template: "<svg width='2000' height='1000'></svg>",
   link: function(scope, elem, attrs){
    scope.$watch('countryInfo', function() {
               if (typeof scope.countryInfo !== 'undefined') {

                   var w = 2000;
                   var h = 1000;
                   var linkDistance=150; // 200

                   var dataset = scope.countryInfo;
                   console.log("in graph directive:", scope.countryInfo)

                   var d3 = $window.d3;
                   var svg = d3.select("body")
                               .selectAll("svg")
                               .attr({"width":w,"height":h})
                               .style('display', 'block')
                               .style('margin','0 auto')
                               .style('overflow-x','scroll');

                   svg.selectAll("*").remove();

//                   var rawSvg = elem.find("svg")[0];
//                   var svg = d3.select(rawSvg);
                   var colors = d3.scale.category10();

                   var force = d3.layout.force()
                           .nodes(dataset.nodes)
                           .links(dataset.edges)
                           .size([w,h])
                           .linkDistance([linkDistance])
                           .charge([-500]) // -500
                           .theta(0.1)
                           .gravity(0.1) // 0.05
                           .start();

                       var edges = svg.selectAll("line")
                         .data(dataset.edges)
                         .enter()
                         .append("line")
                         .attr("id",function(d,i) {return 'edge'+i})
                         .attr('marker-end','url(#arrowhead)')
                         .style("stroke","#ccc")
                         .style("pointer-events", "none");

                       var nodes = svg.selectAll("circle")
                         .data(dataset.nodes)
                         .enter()
                         .append("circle")
                         .attr({"r":8})
                         .style("fill",function(d,i){return colors(d.group);})
                         .call(force.drag)


                       var nodelabels = svg.selectAll(".nodelabel")
                          .data(dataset.nodes)
                          .enter()
                          .append("text")
                          .attr({"x":function(d){return d.x;},
                                 "y":function(d){return d.y;},
                                 "class":"nodelabel",
                                 "stroke":"black"})
                          .text(function(d){return d.name;});

                       var edgepaths = svg.selectAll(".edgepath")
                           .data(dataset.edges)
                           .enter()
                           .append('path')
                           .attr({'d': function(d) {return 'M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y},
                                  'class':'edgepath',
                                  'fill-opacity':0,
                                  'stroke-opacity':0,
                                  'fill':'blue',
                                  'stroke':'red',
                                  'id':function(d,i) {return 'edgepath'+i}})
                           .style("pointer-events", "none");

                       var edgelabels = svg.selectAll(".edgelabel")
                           .data(dataset.edges)
                           .enter()
                           .append('text')
                           .style("pointer-events", "none")
                           .attr({'class':'edgelabel',
                                  'id':function(d,i){return 'edgelabel'+i},
                                  'dx':80,
                                  'dy':0,
                                  'font-size':10,
                                  'fill':'#aaa'});

                       edgelabels.append('textPath')
                           .attr('xlink:href',function(d,i) {return '#edgepath'+i})
                           .style("pointer-events", "none")
                           .text(function(d,i){return d.relation});


                       svg.append('defs').append('marker')
                           .attr({'id':'arrowhead',
                                  'viewBox':'-0 -5 10 10',
                                  'refX':25,
                                  'refY':0,
                                  //'markerUnits':'strokeWidth',
                                  'orient':'auto',
                                  'markerWidth':10,
                                  'markerHeight':10,
                                  'xoverflow':'visible'})
                           .append('svg:path')
                               .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
                               .attr('fill', '#ccc')
                               .attr('stroke','#ccc');


                       force.on("tick", function(){

                           edges.attr({"x1": function(d){return d.source.x;},
                                       "y1": function(d){return d.source.y;},
                                       "x2": function(d){return d.target.x;},
                                       "y2": function(d){return d.target.y;}
                           });

                           nodes.attr({"cx":function(d){return d.x;},
                                       "cy":function(d){return d.y;}
                           });

                           nodelabels.attr("x", function(d) { return d.x; })
                                     .attr("y", function(d) { return d.y; });

                           edgepaths.attr('d', function(d) { var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y;
                                                              //console.log(d)
                                                              return path});

                           edgelabels.attr('transform',function(d,i){
                               if (d.target.x<d.source.x){
                                   bbox = this.getBBox();
                                   rx = bbox.x+bbox.width/2;
                                   ry = bbox.y+bbox.height/2;
                                   return 'rotate(180 '+rx+' '+ry+')';
                                   }
                               else {
                                   return 'rotate(0)';
                                   }
                           });
                       });
                   }
           });
   }
 };
});
