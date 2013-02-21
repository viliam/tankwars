package sk.kave.tank

import actors.GameControllerActor
import beans.Game
import fx.map.GameStage
import scalafx.application.JFXApp
import scalafx.animation.{KeyFrame, Animation, Timeline}
import javafx.event.{ActionEvent, EventHandler}
import scalafx.Includes._
import scalafx.util.Duration

/**
 * User: wilo
 * Date: 2/13/13
 * Time: 1:11 PM
 */
object Main extends JFXApp {
  stage = GameStage

  val controlerActor = (new GameControllerActor(GameStage.mapGroup)).start()

}
