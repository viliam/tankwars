package sk.kave.tank.beans

/**
 * User: wilo
 * Date: 2/13/13
 * Time: 1:12 PM
 */

import sk.kave.tank._


object Map {

  lazy val m: Map = readMapFromFile("mapa.mapa")

  def apply() = m
}

class Map(val items: ROWS) {

  def apply(r: Int, c: Int): Items = {
    if (r >= items.size || c >= items(r).size) {
      return NoMap
    }
    items(r)(c)
  }
}
