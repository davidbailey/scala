import scala.util.Random
import scala.collection.mutable.ListBuffer
import Models._ //:load models.scala
import Polyline.decode //:load Polyline.scala
import io.plasmap.parser.OsmParser // https://github.com/plasmap/geow
//import org.apache.spark.{SparkContext, SparkConf}
import org.apache.kafka.clients.producer.KafkaProducer

object Main {
  val parser = OsmParser("https://s3.amazonaws.com/metro-extracts.mapzen.com/los-angeles_california.osm.bz2")
  val routesFileName = System.getProperty("user.home") + "/Desktop/maps/routes.polylines"
  val routes = scala.io.Source.fromFile(routesFileName).getLines.toList
  val routesDecoded = routes.map(Polyline.decode)
  //val sparkRoutes = sc.textFile(routesFileName)

  var mutablePeople = new ListBuffer[Models.Person]
  var mutableCars = new ListBuffer[Models.Car]
  var mutableBicycles = new ListBuffer[Models.Bicycle]
 
  for ( r <- routesDecoded ) {
    val p = new Models.Person(r)
    mutablePeople += p
    // Replace WalkCarBike with Safety, Time, Comfort, and Cost Choice based on the Person
    val WalkCarBike = Random.nextInt(100)
    if (WalkCarBike <= 80) {
      val c = new Models.Car(p.route{0})
      p.vehicle = Some(c)
      c.driver = Some(p)
      mutableCars += c
    }
    if (80 < WalkCarBike && WalkCarBike < 83) {
      val b = new Models.Bicycle(p.route{0})
      p.vehicle = Some(b)
      b.driver = Some(p)
      mutableBicycles += b
    }
  }

  val People = mutablePeople.toList
  val Cars = mutableCars.toList
  val Bicycles = mutableBicycles.toList

  for (a <- 1 to 10) {
    println("\n\nRound " + a + "\n")
    People.map(p => p.view)
    People.map(p => p.transport)
    Cars.map(c => c.view)
    Bicycles.map(b => b.view)
    //Thread.sleep(1000)
  }
}
