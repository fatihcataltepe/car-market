package com.scout24.carmarket

import java.sql.Date

import slick.dbio.DBIO
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Car(id: Option[Long], title: String, fuel: String, price: Long, isNew: Boolean, mileage: Option[Long], firstReg: Option[Date])


class CarTable(tag: Tag) extends Table[Car](tag, "cars") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def title: Rep[String] = column[String]("title")

  def fuel: Rep[String] = column[String]("fuel")

  def price: Rep[Long] = column[Long]("price")

  def isNew: Rep[Boolean] = column[Boolean]("is_new")

  def mileage: Rep[Long] = column[Long]("mileage")

  def firstReg: Rep[Date] = column[Date]("first_reg")

  def * = (id.?, title, fuel, price, isNew, mileage.?, firstReg.?) <> (Car.tupled, Car.unapply)
}

case class CarRepo() extends {

  val db = Database.forConfig("database")

  val table: TableQuery[CarTable] = TableQuery[CarTable]

  def init: Future[Unit] = db.run(DBIO.seq(table.schema.create))

  def drop: Future[Unit] = db.run(DBIO.seq(table.schema.drop))

  def insert(car: Car): Future[Unit] = db.run(DBIO.seq(table += car))

  def delete(id: Long): Future[Unit] = db.run(DBIO.seq(table.filter(_.id === id).delete))

  def getCar(id: Long): Future[Option[Car]] = db.run(table.filter(_.id === id).result.headOption)

  def getCars: Future[Seq[Car]] = db.run(table.result)

}