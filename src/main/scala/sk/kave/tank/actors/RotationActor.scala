package sk.kave.tank.actors

import scala.actors.{SchedulerAdapter, Actor}
import sk.kave.tank.Config
import sk.kave.tank._
import beans.{Game, Tank}
import fx._
import fx.map.{MapGroup, GameStage}
import scalafx.animation.{Timeline, KeyFrame}
import scalafx.Includes._
import javafx.event.{ActionEvent, EventHandler}

/**
 * actor performing movement of tank.
 *
 * @author Vil
 */
class RotationActor extends JfxActor {
  self =>

  val mapActor = (new MovementActor).start()
  val tank = Game.tank

  private var newVect : Vector2D = tank.vect

  private var isTimelineAlive = false

  def act() {
    link(Main.controlerActor)
    react {
      case (horizontal: Option[Horizontal], vertical: Option[Vertical])  =>
        //if (!isTimelineAlive) {
          newVect = (horizontal, vertical)
          if (newVect != tank.vect ) {
            tank() = newVect
          }

          mapActor ! newVect
//        } else {
//          Main.controlerActor ! Action.CONTINUE
//        }

        act()
      case Action.CONTINUE =>        //when one key si released, actor needs to continue
        Main.controlerActor ! Action.CONTINUE
        act()
    }
  }

}
