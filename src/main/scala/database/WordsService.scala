package database

import cats.effect.IO
import cats.syntax.either._
import doobie.Transactor
import doobie.implicits._
import utils.Errors.{AppError, BaseError}

trait WordsService {
  def find(user_id: String): IO[Either[BaseError, List[Words]]]

  def insert(wordsInfo: Words): IO[Either[BaseError, Int]]
}

object WordsService {
  private final class Impl(wordsDb: WordsDbObject, transactor: Transactor[IO]) extends WordsService {

    override def find(user_id: String): IO[Either[BaseError, List[Words]]] = {
      wordsDb
        .find(user_id)
        .transact(transactor)
        .attempt
        .map(_.leftMap(AppError.apply))
    }

    override def insert(words: Words): IO[Either[BaseError, Int]] = {
      wordsDb.create(words).transact(transactor).attempt.map {
        case Left(th)    => AppError(th).asLeft
        case Right(todo) => todo.asRight
      }
    }

  }

  def make(sql: WordsDbObject, transactor: Transactor[IO]): WordsService = new Impl(sql, transactor)

}
