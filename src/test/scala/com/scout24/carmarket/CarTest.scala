package com.scout24.carmarket

import org.scalatest.FunSpec

class CarTest extends FunSpec {

  describe("CarTest") {

    it("should isValid") {
      //new cars
      assert(true == Car(None, "Audi", "gasoline", 1000, true, None, None).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, true, Some(10), Some(java.sql.Date.valueOf("2017-01-01"))).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, true, None, Some(java.sql.Date.valueOf("2017-01-01"))).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, true, Some(10), None).isValid)

      //used cars
      assert(true == Car(None, "Audi", "gasoline", 1000, false, Some(10), Some(java.sql.Date.valueOf("2017-01-01"))).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, false, None, Some(java.sql.Date.valueOf("2017-01-01"))).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, false, Some(10), None).isValid)
      assert(false == Car(None, "Audi", "gasoline", 1000, false, None, None).isValid)
    }

  }
}
