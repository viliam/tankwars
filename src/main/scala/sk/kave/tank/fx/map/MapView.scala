package sk.kave.tank.fx.map

import sk.kave.tank._
import collection.mutable
import fx._
import collection.immutable.IndexedSeq
import scala.Some
import collection.mutable.ListBuffer

/**
 * Created by IntelliJ IDEA.
 * User: Igo || Vil
 * Date: 18.2.2013
 * Time: 11:55
 */

class MapView[R](val initRec: (Option[R], Int, Int) => R)(implicit config: Config) {
  val BORDER_SIZE = 1 //width of border (in rectangles) around user's view

  //current position of the map group; coordinates are indices in map data model
  var col = 0 - BORDER_SIZE
  var row = 0 - BORDER_SIZE

  def colMax = config.width / config.itemSize + col - 1 + BORDER_SIZE

  def rowMax = config.height / config.itemSize + row - 1 + BORDER_SIZE

  val rows = mutable.Map() ++ (for (i <- col to colMax) yield {
    (i, new ListBuffer[R]())
  })
  val cols = mutable.Map() ++ (for (i <- row to rowMax) yield {
    (i, new ListBuffer[R]())
  })

  def init(): IndexedSeq[R] = {

    for (
      iCol <- col to colMax;
      iRow <- row to rowMax)
    yield {
      val r = initRec(None, iCol, iRow)

      rows(iRow) += r
      cols(iCol) += r
      r
    }
  }

  def canMove(dir: (Option[Horizontal], Option[Vertical])): Boolean = {
    val result =
      (dir._1 match {
        case Some(LEFT) if (col <= 0) =>
          false
        case Some(RIGHT) if (col >= MapGroup.map.maxCols - 1 + BORDER_SIZE - colMax) =>
          false
        case _ => true
      }) &&
        (dir._2 match {
          case Some(UP) if (row <= 0) =>
            false
          case Some(DOWN) if (row >= MapGroup.map.maxRows - 1 + BORDER_SIZE - rowMax) =>
            false
          case _ => true
        })
    if (!result) {
      logg.debug("cannot move")
    }

    result
  }

  def move(d: Option[Direction]) {
    logg.debug("move to direction = " + d + "  on row: " + row + "; col:" + col)

    d match {
      case Some(DOWN) => {
        require(row >= 0 - BORDER_SIZE && row < MapGroup.map.maxRows - 1 + BORDER_SIZE, "row = " + row + " maxRows = " + MapGroup.map.maxRows)
        require(col >= 0 - BORDER_SIZE && col < MapGroup.map.maxCols + BORDER_SIZE, "col = " + col + " maxCols = " + MapGroup.map.maxCols)

        moveVertical(row, rowMax + 1, DOWN)

        row = row + 1
      }
      case Some(UP) => {
        require(row >= 1 - BORDER_SIZE && row < MapGroup.map.maxRows + BORDER_SIZE, "row = " + row + " maxRows = " + MapGroup.map.maxRows)
        require(col >= 0 - BORDER_SIZE && col < MapGroup.map.maxCols + BORDER_SIZE, "col = " + col + " maxCols = " + MapGroup.map.maxCols)

        moveVertical(rowMax, row - 1, UP)

        row = row - 1
      }
      case Some(RIGHT) =>
        require(row >= 0 - BORDER_SIZE && row < MapGroup.map.maxRows + BORDER_SIZE, "row = " + row + " maxRows = " + MapGroup.map.maxRows)
        require(col >= 0 - BORDER_SIZE && col < MapGroup.map.maxCols - 1 + BORDER_SIZE, "col = " + col + " maxCols = " + MapGroup.map.maxCols)

        moveHorizontal(col, colMax + 1, RIGHT)

        col = col + 1
      case Some(LEFT) =>
        require(row >= 0 - BORDER_SIZE && row < MapGroup.map.maxRows + BORDER_SIZE, "row = " + row + " maxRows = " + MapGroup.map.maxRows)
        require(col >= 1 - BORDER_SIZE && col < MapGroup.map.maxCols + BORDER_SIZE, "col = " + col + " maxCols = " + MapGroup.map.maxCols)

        moveHorizontal(colMax, col - 1, LEFT)

        col = col - 1
      case None =>
    }
  }

  private def moveHorizontal(from: Int, to: Int, direction: Horizontal) {
    moveEveryEdgeFromTo(rows, direction == RIGHT)
    val li = moveEdgeFromTo(cols, from, to)
    require(!li.isEmpty)

    val list = li.get
    for (i <- 0 until list.size) {
      initRec(Some(list(i)), to, row + i)
    }
  }

  private def moveVertical(from: Int, to: Int, direction: Vertical) {
    moveEveryEdgeFromTo(cols, direction == DOWN)
    val li = moveEdgeFromTo(rows, from, to)
    require(!li.isEmpty)

    val list = li.get
    for (i <- 0 until list.size) {
      initRec(Some(list(i)), col + i, to)
    }
  }


  private def moveEdgeFromTo(map: mutable.Map[Int, ListBuffer[R]], oldPosition: Int, newPosition: Int): Option[ListBuffer[R]] = {
    val liOption = map.remove(oldPosition) //remove rectangles from their old position

    map(newPosition) = liOption.get //move them to new position

    liOption
  }

  private def moveEveryEdgeFromTo(map: mutable.Map[Int, ListBuffer[R]], dir: Boolean) {

    if (dir) {
      for (k <- map.keys) {
        val firstRect = map(k).remove(0)
        map(k) += firstRect
      }
    } else {
      for (k <- map.keys) {
        val firstRect = map(k).remove(map(k).size - 1)
        firstRect +=: map(k)
      }
    }
  }
}
