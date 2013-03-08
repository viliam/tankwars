package sk.kave.tank.beans

import sk.kave.tank.fx._
import sk.kave.tank._
import events.{TankEvent, EventListener}
import fx.{RIGHT, DOWN, LEFT}

object Tank {

  val transformation = Vector[Vector2D](//clockwise path
    (None, Some(UP)), (Some(LEFT), Some(UP)),
    (Some(LEFT), None), (Some(LEFT), Some(DOWN)),
    (None, Some(DOWN)), (Some(RIGHT), Some(DOWN)),
    (Some(RIGHT), None), (Some(RIGHT), Some(UP)))

  def getAngle(from: Vector2D, to: Vector2D): Double = {
    val i1 = transformation.indexOf(from)
    val i2 = transformation.indexOf(to)

    val n = (i1 - i2)

    //sometimes clockwise direction isn't shortest path
    if (math.abs(n) > transformation.size / 2) {
      (transformation.size - math.abs(n)) * math.signum(n) * -45
    }
    else {
      n * 45
    }
  }

  def isInPosition(x: Int, y: Int)(implicit gContext: Game): (Boolean, Boolean) = {
    import gContext.config._

    val h = if ((x > Map().maxCols - width / 2)  || (x < width / 2)) false else true
    val v = if ((y > Map().maxRows - height / 2) || (y < height / 2)) false else true

    (h, v)
  }
}


trait Tank extends EventListener[TankEvent] {

  def direction : Vector2D

  def changeDirection(direction: Vector2D)(callBack: () => Unit) : Unit

  def move(vect: Vector2D)(callback: () => Unit) : Unit

  def canMove(direction: Vector2D) : Boolean
}
