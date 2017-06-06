package com.scout24.carmarket


import java.sql.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}


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
            val getCarsF: Future[Seq[Car]] = repo.getCars
            onComplete(getCarsF) {
              case Success(x) if sortType == "title" => complete(StatusCodes.OK, x.sortBy(_.title))
              case Success(x) if sortType == "fuel" => complete(StatusCodes.OK, x.sortBy(_.fuel))
              case Success(x) if sortType == "price" => complete(StatusCodes.OK, x.sortBy(_.price))
              case Success(x) if sortType == "isNew" => complete(StatusCodes.OK, x.sortBy(_.isNew))
              case Success(x) if sortType == "mileage" => complete(StatusCodes.OK, x.sortBy(_.mileage))
              case Success(x) if sortType == "firstReg" => complete(StatusCodes.OK, x.sortBy(_.firstReg.map(_.getTime)))
              case Success(x) => complete(StatusCodes.OK, x.sortBy(_.id))
              case Failure(e) => complete(CustomStatusCodes.create500(e))
            }
          }
        } ~
          post {
            entity(as[Car]) { car => // will unmarshal JSON to Order
              val insertF: Future[Unit] = repo.insert(car)
              onComplete(insertF) {
                case Success(_) => complete(StatusCodes.OK)
                case Failure(e) => complete(CustomStatusCodes.create400(e))
              }
            }
          }
      } ~
        path("cars" / LongNumber) { id =>
          get {
            val getCarF: Future[Option[Car]] = repo.getCar(id)
            onComplete(getCarF) {
              case Success(Some(x)) => complete(StatusCodes.OK, x)
              case Success(None) => complete(CustomStatusCodes.create404("The requested car could not found in the database"))
              case Failure(e) => complete(CustomStatusCodes.create500(e))
            }
          } ~
            delete {
              val deleteF: Future[Unit] = repo.delete(id)
              onComplete(deleteF) {
                case Success(_) => complete(StatusCodes.OK)
                case Failure(e) => complete(CustomStatusCodes.create500(e))
              }
            } ~
            put {
              entity(as[Car]) { car =>
                val updateF: Future[Int] = repo.updateCar(id, car)
                onComplete(updateF) {
                  case Success(1) => complete(StatusCodes.OK)
                  case Success(0) => complete(CustomStatusCodes.create500("An error occured in the database"))
                  case Failure(e) => complete(CustomStatusCodes.create500(e))
                }
              }
            }
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }


}
