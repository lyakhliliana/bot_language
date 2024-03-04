package database

import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Query0, Update0}

trait WordsDbObject {
  def find(user_id: String): ConnectionIO[List[Words]]

  def create(expense: Words): ConnectionIO[Int]
}

object WordsDbObject {
  private object queries {
    def findSql(user_id: String): Query0[Words] =
      sql"""
      SELECT user_id, first, second FROM pair_word
      WHERE user_id = $user_id;""".query[Words]

    def insertSql(word: Words): Update0 = {
      sql"""
      INSERT INTO pair_word (user_id, first, second)
      VALUES (${word.user_id}, ${word.first}, ${word.second});
      """.update
    }
  }

  private final class Impl extends WordsDbObject {

    import WordsDbObject.queries._

    override def find(user_id: String): ConnectionIO[List[Words]] = findSql(user_id).to[List]
    override def create(word: Words): ConnectionIO[Int] = insertSql(word).run
  }

  def make: WordsDbObject = new Impl
}
