package com.scout24.carmarket


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}


class WebServer(props: WebServerProps) extends LazyLogging with JsonSupport {

  def run: Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val repo: CarRepo = CarRepo()

    val route =
      path("cars") {
        get {
          parameter("sort" ? "id") { sortType =>
            logger.debug(s"Method: GET, route: /cars, sort: $sortType")
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
            entity(as[Car]) { car =>
              logger.debug(s"Method: POST, route: /cars, car: $car")
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
            logger.debug(s"Method: GET, route: /cars/$id")
            val getCarF: Future[Option[Car]] = repo.getCar(id)
            onComplete(getCarF) {
              case Success(Some(x)) => complete(StatusCodes.OK, x)
              case Success(None) => complete(CustomStatusCodes.create404("The requested car could not found in the database"))
              case Failure(e) => complete(CustomStatusCodes.create500(e))
            }
          } ~
            delete {
              logger.debug(s"Method: DELETE, route: /cars/$id")
              val deleteF: Future[Unit] = repo.delete(id)
              onComplete(deleteF) {
                case Success(_) => complete(StatusCodes.OK)
                case Failure(e) => complete(CustomStatusCodes.create500(e))
              }
            } ~
            put {
              entity(as[Car]) { car =>
                logger.debug(s"Method: PUT, route: /cars/$id car: $car")
                val updateF: Future[Int] = repo.updateCar(id, car)
                onComplete(updateF) {
                  case Success(1) => complete(StatusCodes.OK)
                  case Success(0) => complete(CustomStatusCodes.create500("An error occured in the database"))
                  case Failure(e) => complete(CustomStatusCodes.create500(e))
                }
              }
            }
        }

    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, props.host, props.port)

    bindingFuture.onComplete {
      case Success(x) => logger.info(s"Web server started at http://${props.host}:${props.port}")
      case Failure(e) => logger.error("Web server crashed", e)

    }
  }


}

object WebServer {
  def apply(props: WebServerProps): WebServer = new WebServer(props)
}
