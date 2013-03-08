package sk.kave.tank.actors


import sk.kave.tank._
import beans.Game
import akka.actor.Actor
import utils.Logger

/**
 * actor performing movement of tank.
 *
 * @author Vil
 */
class TankActor extends Actor with Logger {

  val gContext = implicitly[Game]
  import gContext._

  private var direction: Vector2D = tank.vect
  private var lock = false

  def receive = {
    case NewDirection(newDirection: Vector2D) =>
      if (!lock) {
        debug("TankActor:  lock", Vilo)
        lock = true

        direction = newDirection
        if (direction != tank.vect) {
          tank.changeDirection(direction) {
            () => {
              self ! UnLock
              if (tank.vect.isDefined) {
                Main.controlerActor ! ContinueMovement(tank.vect)
              }
            }
          }
        } else {
          tank.move(direction) {
            () => {
              self ! UnLock
              if (tank.vect.isDefined) {
                Main.controlerActor ! ContinueMovement(tank.vect)
              }
            }
          }
        }
      } else {
        debug("TankActor: message is ignoring " + newDirection, All)
      }

    case UnLock => //when one key si released, actor needs to continue
      lock = false
      debug("TankActor: unlock actor" + direction, Vilo)

    case m@AnyRef => warn("TankActor : Unknow message = " + m, All)
  }
}
