package bot

import cats.effect.{Concurrent, ContextShift}
import cats.syntax.functor._
import com.bot4s.telegram.api.declarative.{Callbacks, Commands, RegexCommands}
import com.bot4s.telegram.cats.Polling
import com.bot4s.telegram.methods.SendMessage
import com.bot4s.telegram.models.{Message, User}
import database.{Words, WordsService}
import utils.Errors.BaseError
import services.{CheckTextProperties, TranslateService}
import utils.{MarkupForReply, Phrases}

trait wordsServiceController {
  def find(user_id: String): Either[BaseError, List[Words]]
  def insert(words: Words): Either[BaseError, Int]
}

final class LanguageBot[F[_]: Concurrent: ContextShift](
  token: String,
  wordsService: WordsService
) extends BaseBot[F](token)
  with Polling[F]
  with Commands[F]
  with RegexCommands[F]
  with Callbacks[F]
  with wordsServiceController {

  private val translateService = TranslateService()

  override def receiveMessage(msg: Message): F[Unit] =
    msg.text.fold(unit) { text =>
      if (!CheckTextProperties.checkOnCommand(text))
        translateService
          .translate(text)
          .map(message =>
            request(
              SendMessage(
                msg.source,
                message,
                replyToMessageId = Some(msg.messageId),
                replyMarkup = Some(MarkupForReply.getTranslateMarkup)
              )
            )
          )
          .unsafeRunSync()
          .void
      else unit
    }

  onCommand(filter = "translate") { implicit msg =>
    withArgs {
      case text if text.nonEmpty =>
        translateService
          .translate(text.mkString)
          .map(reply(_, replyMarkup = Some(MarkupForReply.getTranslateMarkup)))
          .unsafeRunSync()
          .void
      case text if text.isEmpty => reply(Phrases.getTranslateEmptyInputMessage).void
      case _                    => reply(Phrases.getUnknownErrorMessage).void
    }
  }

  onCommand("start") { implicit msg =>
    msg.from match {
      case Some(user: User) =>
        reply(Phrases.getStartMessage(user.firstName), replyMarkup = Some(MarkupForReply.getCommandMarkup)).void
      case _ => reply(Phrases.getErrorUsernameMessage).void
    }
  }

  onCommand("commands") { implicit msg =>
    msg.from match {
      case Some(user: User) =>
        reply(Phrases.getCommandMessage(user.firstName), replyMarkup = Some(MarkupForReply.getCommandMarkup)).void
      case _ => reply(Phrases.getErrorUsernameMessage).void
    }

  }

  onCallbackQuery { implicit callBackQuery =>
    {
      val chatId = callBackQuery.message.get.chat.id
      callBackQuery.data match {
        case Some("stat") =>
          val list = find(chatId.toString) match {
            case Left(_)       => List()
            case Right(result) => result
          }
          request(SendMessage(chatId, Phrases.getStatisticsMessage(list))).void

        case Some("help") => request(SendMessage(chatId, Phrases.getFullHelpMessage)).void
        case Some("save") =>
          val trans = callBackQuery.message.get.text.get
          val to_trans = callBackQuery.message.get.replyToMessage.get.text.get
          val wordToInsert = Words(chatId.toString, to_trans, trans)

          insert(wordToInsert) match {
            case Left(_)  => request(SendMessage(chatId, Phrases.getUnknownErrorMessage)).void
            case Right(_) => unit
          }

        case Some("not save") => unit
        case Some("nothing")  => request(SendMessage(chatId, Phrases.getNothingMessage)).void
        case _                => unit
      }
    }
  }

  override def find(user_id: String): Either[BaseError, List[Words]] = {
    wordsService.find(user_id).unsafeRunSync()
  }

  override def insert(words: Words): Either[BaseError, Int] = {
    wordsService.insert(words).unsafeRunSync()
  }
}
