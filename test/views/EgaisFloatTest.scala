package views

import org.scalatest.FunSuite

/**
  * Created by seriousdron on 08.10.17.
  */
class EgaisFloatTest extends FunSuite {

  test("testFormat") {
    assert(EgaisFloat.format(12.34567f) === "12.3457")
    assert(EgaisFloat.format(13f) === "13.0000")
    assert(EgaisFloat.format(.435f) === "0.4350")
    assert(EgaisFloat.format(23526236.435) === "23526236.4350")
  }

}
