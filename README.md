## Knowledge Browser
- A UI Dashboard built on top of Spark to browse knowledge (a.k.a data) 
- Real-time query spark and visualise it as graph.
- Supports SQL query syntax.
- This is just a sample application to get an idea on how to go about building any kind of analytics dashboard on top of the data that Spark processed. One can customize it according to their needs.

## Demo
![out](https://user-images.githubusercontent.com/22542670/28935434-31a77148-78a2-11e7-97d6-3267f3cd2b16.gif)

- When you run this project, the dashboard page will look something like the one shown above.
- User can type his query and hit submit.
- Upon submit, spark processes it and returns the data as JSON.
- The json result is rendered as graph using D3 and AngularJS.
- Above demo illustrated a simple country profile search `CountryCode IN ("USA", "IND", "WLD")` and how the three countries information is displayed as graph. Notice, any common relationship between 2 countries are linked via a common node.

## Technology Stack
<img width="574" src="https://user-images.githubusercontent.com/22542670/28937262-f33f9a38-78a7-11e7-9767-e2791564cce6.png">

## Running this project
- Build: `mvn clean install`
- Run: `spark-submit --class MainApp graph-knowledge-browser.jar`
- Go to browser and start querying @ http://localhost:8002/index.html

## What to Query?
I've used http://data.worldbank.org open data countries profile information as knowledge base in this project. 

### Sample Data: 
Following table displays some sample rows to give an idea on the columns and schema of the data taken as knowledge base:
<img width="873" alt="screen shot 2017-08-03 at 11 45 41 pm" src="https://user-images.githubusercontent.com/22542670/28936625-e4cf32e4-78a5-11e7-99f6-cdec6b93ce71.png">

## Sample Queries to run (supports sql syntax):
Query USA profile: 
- `CountryCode = 'USA'`

Query all countries with name starting with letter 'I':
- `CountryCode LIKE 'I%'`

Query to get India, USA and World's profile info:
- `CountryCode IN ('USA', 'WLD', 'IND')`

Query total population, mortality rate and population growth information in India, USA and World countries: 
- `CountryCode IN ('USA', 'WLD', 'IND') AND SeriesCode IN ('SP.POP.TOTL', 'SH.DYN.MORT', 'SP.POP.GROW')`

## cmd-line args:
Optionally, you can provide configuration params like the host and port from command line. To see the list of configurable params, just type:
`$ spark-submit <path-to-graph-knowledge-browser.jar> --help`

Help content will look something like this:
```markdown
    Apart from Spark, this application uses akka-http from browser integration.
    So, it needs config params like AkkaWebPort to bind to, SparkMaster
    and SparkAppName

    Usage: spark-submit graph-knowledge-browser.jar [options]
      Options:
      -h, --help
      -m, --master <master_url>                    spark://host:port, mesos://host:port, yarn, or local. Default: $sparkMasterDef
      -n, --name <name>                            A name of your application. Default: $sparkAppNameDef
      -p, --akkaHttpPort <portnumber>              Port where akka-http is binded. Default: $akkaHttpPortDef

    Configured one route:
    1. http://host:port/index.html - takes user to knowledge browser page
```
    
## Structure of the project:
- <b>src/main/scala/com/spoddutur/MainApp.scala:</b> The main class from where application execution begins
- <b>data/countriesProfile.csv</b> sample data used to query
- <b>src/main/resources/application.conf</b>: tweak command line args directly here before building the jar and run spark-submit
- <b>src/main/scala/com/spoddutur/web/WebServer.scala</b>: Starts akka-http webserver at the mentioned host and port. Also, registers the routes (index.html). 
- <b>src/main/scala/com/spoddutur/web/Router.scala:</b>: This is where we can create and register more routes apart from index.html

### D3 references:
- https://bl.ocks.org/mbostock
- http://bl.ocks.org/jhb
