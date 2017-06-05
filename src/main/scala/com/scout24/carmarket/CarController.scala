package com.scout24.carmarket

import collection.JavaConverters._
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

@RestController
@RequestMapping(value = Array("/cars"))
class CarController {

  private val repo: CarRepo = CarRepo()

  @GetMapping(value = Array("/{id}"))
  def getCar(@PathVariable id: Long) = {
    val future: Future[Option[Car]] = repo.getCar(id)
    Await.result(future, 3.seconds) match {
      case Some(x) => Json.stringify(Json.toJson(x))
      case None => new ResponseEntity[String](s"""{"message": "Car does not exist"}""", HttpStatus.BAD_REQUEST)
    }
  }

  @DeleteMapping(value = Array("/{id}"))
  def deleteCar(@PathVariable id: Long) = {
    val future: Future[Unit] = repo.delete(id)
    future.onComplete{
      case Success(x)=> new ResponseEntity(HttpStatus.OK)
      case Failure(x)=> new ResponseEntity[String](s"""{"message": "$x"}""", HttpStatus.BAD_REQUEST)
    }
    Await.result(future, 3.seconds)
  }

  @PostMapping
  def createCar(@RequestBody car: Car)= {
    val future: Future[Unit] = repo.insert(car)
    future.onComplete{
      case Success(x)=> new ResponseEntity(HttpStatus.OK)
      case Failure(x)=> new ResponseEntity[String](s"""{"message": "$x"}""", HttpStatus.INTERNAL_SERVER_ERROR)
    }
    Await.result(future, 3.seconds)
  }

}

