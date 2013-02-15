/*
 * Copyright viliam.kois@gmail.com Kois Viliam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package sk.kave.tank.fx

import actors.Actor
import scalafx.animation.Timeline
import javafx.event.EventHandler
import javafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Group
import sk.kave.tank._
import sun.awt.VerticalBagLayout


/*

This actor will be responsible for handling events. This asynchronized processing of events make processing events
synchronized.

 */

object Action extends Enumeration {
  val DOWN, LEFT, RIGHT, UP, EXIT = Value
}

sealed trait Direction

trait Vertical extends Direction

case object DOWN extends Vertical

case object UP extends Vertical

trait Horizontal extends Direction

case object LEFT extends Horizontal

case object RIGHT extends Horizontal

case object NoneDir extends Direction with Horizontal with Vertical


class GameControlerActor(val mapGroup: Group) extends Actor {
  self =>

  var direction: (Horizontal, Vertical) = (NoneDir, NoneDir)
  var (horizontal, vertical) = direction

  def act() {
    react {
      case (Action.EXIT, KeyPressEvent.RELEASED) =>
        logg.info("actor says 'Good bye'")
      case (a: Action.Value, kpe: KeyPressEvent.Value) =>
        runInJFXthred(move(a, kpe))
        act()
    }
  }

  private def isMoving: Boolean = horizontal != NoneDir || vertical != NoneDir


  private def getDirectionHorizontal =
    horizontal match {
      case LEFT => +ItemSize
      case RIGHT => -ItemSize
      case NoneDir => 0
    }

  private def getDirectionVertical =
    vertical match {
      case UP => +ItemSize
      case DOWN => -ItemSize
      case NoneDir => 0
    }

  private def translateX = mapGroup.translateX

  private def translateY = mapGroup.translateY


  private def setAction(newDirection: Direction, kpe: KeyPressEvent.Value): Direction = {
    val v = if (kpe == KeyPressEvent.RELEASED) {
      NoneDir
    } else {
      newDirection
    }
    logg.debug("setAction = " + v)
    v
  }

  private implicit def convertDirection2Vertical(dir: Direction): Vertical =
    if (dir != null) {
      dir.asInstanceOf[Vertical]
    } else {
      null
    }

  private implicit def convertDirection2Horizontal(dir: Direction): Horizontal =
    if (dir != null) {
      dir.asInstanceOf[Horizontal]
    } else {
      null
    }

  private def move(action: Action.Value, kpe: KeyPressEvent.Value) {
    logg.debug(action)

    action match {
      case Action.UP =>
        vertical = setAction(UP, kpe)

      case Action.DOWN =>
        vertical = setAction(DOWN, kpe)

      case Action.LEFT =>
        horizontal = setAction(LEFT, kpe)

      case Action.RIGHT =>
        horizontal = setAction(RIGHT, kpe)
    }

    //    if (isMoving) {
    //isMoving = true

    logg.debug(action + " pohyb: horizontal = " + getDirectionHorizontal + "; vertical = " + getDirectionVertical)

    new Timeline() {
      onFinished = new EventHandler[ActionEvent] {
        def handle(e: ActionEvent) {
          //isMoving = false
        }
      }


      keyFrames = Seq(
        at(0 ms) {
          Set(translateX -> translateX(),
            translateY -> translateY())
        },
        at(10 ms) {
          Set(translateX -> (translateX() + getDirectionHorizontal),
            translateY -> (translateY() + getDirectionVertical))
        }
      )
    }.play
  }

  //  }

  private def runInJFXthred(runThis: => Unit) {
    javafx.application.Platform.runLater(new Runnable() {
      def run() = runThis
    })
  }
}


