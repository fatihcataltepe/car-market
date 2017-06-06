package com.scout24.carmarket


import java.sql.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object DateJsonFormat extends RootJsonFormat[Date] {

    override def read(json: JsValue): Date = json match {
      case JsString(s) => Date.valueOf(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }

    override def write(obj: Date): JsValue = JsString(obj.toString)
  }

  implicit val carFormat = jsonFormat7(Car)

}

object WebServer extends JsonSupport {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val repo: CarRepo = CarRepo()

    val route =
      path("cars") {
        get {
          parameter("sort" ? "id") { sortType =>
            val result: Seq[Car] = Await.result(repo.getCars, 3.seconds)
            sortType match {
              case "title" => complete(result.toList.sortBy(_.title))
              case "fuel" => complete(result.toList.sortBy(_.fuel))
              case "price" => complete(result.toList.sortBy(_.price))
              case "isNew" => complete(result.toList.sortBy(_.isNew))
              case "mileage" => complete(result.toList.sortBy(_.mileage))
              case "firstReg" => complete(result.toList.sortBy(_.firstReg.map(_.getTime)))
              case _ => complete(result.toList.sortBy(_.id))
            }
          }
        } ~
          post {
            entity(as[Car]) { car => // will unmarshal JSON to Order
              Await.result(repo.insert(car), 3.seconds)
              complete("")
            }
          }
      }

    val route2 =
      path("cars" / LongNumber) { id =>
        get {
          val result: Option[Car] = Await.result(repo.getCar(id), 3.seconds)
          result match{
            case Some(x) => complete(x)
            case None => complete("")
          }
        }~
          delete {
            val result: Unit = Await.result(repo.delete(id), 3.seconds)
            complete("")
        }~
          put {
            entity(as[Car]) { car =>
              Await.result(repo.updateCar(id,car), 3.seconds)
              complete("")
            }
        }
      }

    val bindingFuture = Http().bindAndHandle(route ~ route2, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }


}
