import bot.LanguageBot
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import database.{DatabaseInit, WordsDbObject, WordsService}
import doobie.Transactor
import doobie.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val dbFilePath = "src/main/data/words.db"
    Class.forName("org.sqlite.JDBC")
    val transactor = Transactor.fromDriverManager[IO](
      "org.sqlite.JDBC",
      s"jdbc:sqlite:$dbFilePath",
      "",
      ""
    )
    DatabaseInit.initializeDb().transact(transactor).unsafeRunSync()
    val wordsService = WordsService.make(WordsDbObject.make, transactor)
    new LanguageBot[IO](sys.env("TELEGRAM_BOT_TOKEN"), wordsService).startPolling().map(_ => ExitCode.Success)

  }
}
