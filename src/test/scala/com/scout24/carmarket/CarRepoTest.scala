package com.scout24.carmarket

import java.sql.Date

import org.scalatest.FunSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class CarRepoTest extends FunSpec {

  private val repo: CarRepo = CarRepo()

  describe("CarRepoTest") {

    it("should drop") {
      val f: Future[Unit] = repo.drop
      f.onComplete {
        case Success(x) => assert(true, x)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 3.seconds)
    }

    it("should init") {
      val f: Future[Unit] = repo.init
      f.onComplete {
        case Success(x) => assert(true, x)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 3.seconds)
    }

    it("should delete") {
      val f2: Future[Unit] = repo.delete(2)
      f2.onComplete {
        case Success(x) => assert(true, x)
        case Failure(x) => assert(false, x)
      }

      Await.result(f2, 3.seconds)
    }

    it("should insert new car") {
      val car: Car = Car(None, "Ford Mustang", "gasoline", 40000, true, None, None)
      val f: Future[Unit] = repo.insert(car)
      f.onComplete {
        case Success(x) => assert(true, x)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 3.seconds)
    }

    it("should insert used car") {
      val car: Car = Car(None, "Ford Mustang", "gasoline", 40000, false, Some(10000), Some(Date.valueOf("2013-01-01")))
      val f: Future[Unit] = repo.insert(car)
      f.onComplete {
        case Success(x) => assert(true, x)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 3.seconds)
    }

    it("should getCars") {
      val f: Future[Seq[Car]] = repo.getCars
      f.onComplete {
        case Success(x) => assert(x.nonEmpty)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 3.seconds)
    }

    it("should getCar") {
      val f: Future[Option[Car]] = repo.getCar(1)
      f.onComplete {
        case Success(x) => assert(x.nonEmpty)
        case Failure(x) => assert(false, x)
      }

      Await.result(f, 10.seconds)
    }

  }
}
