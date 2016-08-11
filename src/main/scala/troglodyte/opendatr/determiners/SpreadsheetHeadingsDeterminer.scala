package troglodyte.opendatr.determiners

import troglodyte.opendatr.askers.Asker

import scala.Function._

class SpreadsheetHeadingsDeterminer(asker: Asker) {
  def determineHeadings(rows: List[(List[Any], Int)]): Option[Int] = {
    val filteredRows = applyRowHeuristics(rows)

    proposeRows("Which of these options looks most like headings to you?", filteredRows.take(3)).orElse(
      proposeRows("How about these?", applyGenerousRowHeuristics(rows))
    )
  }

  private def proposeRows(message: String, rows: List[(List[Any], Int)]): Option[Int] = {
    asker.chooseOrRefuse('pick_headings_row, message,
      rows.map(tupled { (r, _) => r.mkString(", ") })).map(n => rows(n)._2)
  }

  private def applyRowHeuristics = Function.chain(List(
    trimRows(_),
    eliminateBlankRows(_),
    eliminateGappyRows(_),
    orderByClosenessToMedian(_)
  ))

  private def applyGenerousRowHeuristics = Function.chain(List(
    trimRows(_),
    eliminateBlankRows(_),
    orderByClosenessToMedian(_)
  ))

  private def trimRows(rows: List[(List[Any], Int)]): List[(List[Any], Int)] = {
    rows.map(tupled { (row, idx) =>
      (
        row
          .dropWhile(cell => cell.toString.trim.isEmpty)
          .reverse
          .dropWhile(cell => cell.toString.trim.isEmpty)
          .reverse,
        idx
        )
    })
  }

  private def eliminateBlankRows(rows: List[(List[Any], Int)]): List[(List[Any], Int)] = {
    rows.filterNot(tupled { (row, _) => row.mkString("").trim.isEmpty })
  }

  private def eliminateGappyRows(rows: List[(List[Any], Int)]): List[(List[Any], Int)] = {
    rows.filterNot(tupled { (row, _) =>
      row // e.g. ["", "", 1, 2, "", 3, 4]
        .dropWhile(cell => cell.toString.trim.isEmpty) // Drop the initial blanks: [1, 2, "", 3, 4]
        .dropWhile(cell => cell.toString.trim.nonEmpty) // Take until we get a blank: ["", 3, 4]
        .exists(cell => cell.toString.trim.nonEmpty) // Is there anything non-blank left? If so, that's a gap
    })
  }

  private def orderByClosenessToMedian(rows: List[(List[Any], Int)]): List[(List[Any], Int)] = {
    if (rows.length < 2) { return rows } // Needn't bother if < 2 rows since median won't be useful
    val median = rows.map(t => t._1.length).sortWith(_ < _).drop(rows.length/2).head // map to lengths, sort, then drop half, get head — median!
    rows.sortBy(tupled { (row, _) =>
      Math.abs(row.length - median) // sort by difference between row length & median
    })
  }
}
