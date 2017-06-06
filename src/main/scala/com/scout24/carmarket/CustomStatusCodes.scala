package com.scout24.carmarket

import akka.http.scaladsl.model.StatusCodes

object CustomStatusCodes {

  def create500(throwable: Throwable) = StatusCodes.custom(500, "Internal Server Error", throwable.getMessage)

  def create500(message: String) = StatusCodes.custom(500, "Internal Server Error", message)

  def create404(throwable: Throwable) = StatusCodes.custom(404, "Not Found", throwable.getMessage)

  def create404(message: String) = StatusCodes.custom(400, "Not Found", message)

  def create400(throwable: Throwable) = StatusCodes.custom(400, "Bad Request", throwable.getMessage)

  def create400(message: String) = StatusCodes.custom(400, "Bad Request", message)

}
