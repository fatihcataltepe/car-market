package com.scout24.carmarket.ws

import java.sql.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.scout24.carmarket.db.Car
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  //converter for java.sql.Date
  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    override def write(obj: Date): JsValue = JsString(obj.toString)

    override def read(json: JsValue): Date = json match {
      case JsString(s) => Date.valueOf(s)
      case _ => throw new DeserializationException("Date must be an instance of JSString")
    }
  }

  //converter for car
  implicit val carFormat = jsonFormat7(Car)
}