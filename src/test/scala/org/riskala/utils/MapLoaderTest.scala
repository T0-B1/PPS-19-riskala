package org.riskala.utils

import org.junit.runner.RunWith
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MapLoaderTest extends AnyWordSpec {

  "An unknown map" should {
    "not be loaded" in {
      assert(MapLoader.loadMap("").isEmpty)
    }
  }

  "A map" should {
    "be correctly loaded" in {
      val optMap = MapLoader.loadMap("italy")
      assert(optMap.isDefined)
      val map = optMap.get
      assert(map.states.size.equals(20))
    }
  }

}
