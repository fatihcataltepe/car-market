package com.scout24

import play.api.libs.json.{Format, Json}

package object carmarket {

  implicit val carFormatter: Format[Car] = Json.format[Car]

}
