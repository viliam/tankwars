package sk.kave.tank.fx

import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene._
import shape.Rectangle

import sk.kave.tank._
import javafx.scene.paint.Color
import sk.kave.tank.beans._
import javafx.event.EventHandler
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.stage.WindowEvent

/**
 * User: wilo
 * Date: 2/13/13
 * Time: 1:11 PM
 */
object MapStage extends Stage {

  title = "ScalaFX Tetris"

  val map = Map()

  val mapGroup = MapGroup

  val controlerActor = (new GameControlerActor(mapGroup)).start()

  scene = new Scene(Width , Height ) {
    fill = Color.BLACK
    content = List(
      mapGroup
    )

    onKeyPressed = new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) {
        logg.debug(e.getCharacter)
        e.code match {
          case (KeyCode.W) => controlerActor !(Action.UP, KeyPressEvent.PRESSED)
          case (KeyCode.A) => controlerActor !(Action.LEFT, KeyPressEvent.PRESSED)
          case (KeyCode.D) => controlerActor !(Action.RIGHT, KeyPressEvent.PRESSED)
          case (KeyCode.S) => controlerActor !(Action.DOWN, KeyPressEvent.PRESSED)
          case _ => ()
        }
      }
    }

    onKeyReleased = new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) {
        logg.debug(e.getCharacter+" rel")
        e.code match {
          case (KeyCode.W) => controlerActor !(Action.UP, KeyPressEvent.RELEASED)
          case (KeyCode.A) => controlerActor !(Action.LEFT, KeyPressEvent.RELEASED)
          case (KeyCode.D) => controlerActor !(Action.RIGHT, KeyPressEvent.RELEASED)
          case (KeyCode.S) => controlerActor !(Action.DOWN, KeyPressEvent.RELEASED)
          case (KeyCode.ESCAPE) => controlerActor !(Action.EXIT, KeyPressEvent.RELEASED)
          case _ => ()
        }
      }
    }
  }

  onHiding = new EventHandler[WindowEvent] {
    def handle(e: WindowEvent) {
      controlerActor !(Action.EXIT, KeyPressEvent.PRESSED)
    }
  }
}

object KeyPressEvent extends Enumeration {
  val PRESSED, RELEASED = Value
}
