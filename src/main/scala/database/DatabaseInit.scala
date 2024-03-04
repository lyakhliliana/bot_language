package database

import cats.implicits.catsSyntaxFlatMapOps
import doobie.ConnectionIO
import doobie.implicits._

object DatabaseInit {
  private def deleteTable(): ConnectionIO[Int] = {
    sql"""DROP TABLE IF EXISTS pair_word;""".update.run
  }

  private def createTable(): ConnectionIO[Int] = {
    sql"""CREATE TABLE pair_word (
        user_id   VARCHAR         NOT NULL,
        first     VARCHAR         NOT NULL,
        second    VARCHAR         NOT NULL
      );""".update.run
  }

  def initializeDb(): ConnectionIO[Int] = {
    deleteTable >> createTable
  }
}
