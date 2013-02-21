package sk.kave.tank.fx.map

import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene._
import image.{Image, ImageView}
import shape.Rectangle

import sk.kave.tank._
import actors.Action
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
object GameStage extends Stage {

  val config = implicitly[Config]
  import config._

  title = "ScalaFX Tetris"

  val map = Game.map
  val tank = Game.tank

  val tankNode = new ImageView {
    image = new Image( GameStage.getClass.getResource( "/tank.png").toString )
    x = tank.x  * itemSize
    y = tank.y  * itemSize
    fitWidth = config.tankSize * config.itemSize
    fitHeight = config.tankSize * config.itemSize
  }

  val mapGroup = MapGroup
  mapGroup.init()

  scene = new Scene(config.width * itemSize, config.height * itemSize) {
    fill = Color.BLACK
    content = List(
      mapGroup,
      tankNode
    )

    onKeyPressed = new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) {
        e.code match {
          case (KeyCode.W) => Main.controlerActor !(Action.UP, KeyPressEvent.PRESSED)
          case (KeyCode.A) => Main.controlerActor !(Action.LEFT, KeyPressEvent.PRESSED)
          case (KeyCode.D) => Main.controlerActor !(Action.RIGHT, KeyPressEvent.PRESSED)
          case (KeyCode.S) => Main.controlerActor !(Action.DOWN, KeyPressEvent.PRESSED)
          case _ => ()
        }
      }
    }

    onKeyReleased = new EventHandler[KeyEvent] {
      def handle(e: KeyEvent) {
        e.code match {
          case (KeyCode.W) => Main.controlerActor !(Action.UP, KeyPressEvent.RELEASED)
          case (KeyCode.A) => Main.controlerActor !(Action.LEFT, KeyPressEvent.RELEASED)
          case (KeyCode.D) => Main.controlerActor !(Action.RIGHT, KeyPressEvent.RELEASED)
          case (KeyCode.S) => Main.controlerActor !(Action.DOWN, KeyPressEvent.RELEASED)
          case (KeyCode.ESCAPE) => Main.controlerActor !(Action.EXIT, KeyPressEvent.RELEASED)
          case _ => ()
        }
      }
    }
  }

  onHiding = new EventHandler[WindowEvent] {
    def handle(e: WindowEvent) {
      Main.controlerActor !(Action.EXIT, KeyPressEvent.PRESSED)
    }
  }
}

object KeyPressEvent extends Enumeration {
  val PRESSED, RELEASED = Value
}
