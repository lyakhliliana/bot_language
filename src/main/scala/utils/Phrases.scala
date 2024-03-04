package utils

import com.bot4s.telegram.models.{InlineKeyboardButton, InlineKeyboardMarkup}
import database.Words

import scala.annotation.tailrec

object Phrases {
  def getStartMessage(username: String): String = {
    s"""
       |Привет, $username!
       |
       |Для перевода нужно:
       |* написать свое предложение на русском языке
       |* использовать синтаксис /translate <фраза>
       |
       |Если нужна небольшая история введенных слов, то просто напиши "/commands" 👀
            """.stripMargin
  }

  def getErrorUsernameMessage: String = "Ошибочка. Что-то не так с твоим именем \uD83E\uDD79"

  def getTranslateEmptyInputMessage: String = "Напиши хоть что-то после команды для перевода\uD83E\uDD96"

  def getUnknownErrorMessage: String = "Что-то пошло не так \uD83E\uDEB4"

  def getCommandMessage(username: String): String = s"""$username, выбери команду из предложенных🪴"""

  def getFullHelpMessage: String = {
    s"""
       |Для перевода нужно:
       |* написать свое предложение на русском языке
       |* использовать синтаксис /translate <фраза>
       |
       |Для вывода истории запросов:
       |* написать "/commands" и выбрать соответственную кнопку)
              """.stripMargin
  }

  def getStatisticsMessage(list: List[Words]): String = {
    if (list.isEmpty) "Тут ничего нет\uD83D\uDE22"
    else {
      @tailrec
      def rec(str: String, i: Int, list: List[Words]): String =
        list match {
          case head :: tail => rec(str + s"""$i) ${head.first} --- ${head.second}\n""", i + 1, tail)
          case Nil          => str
        }
      val head = "Последние переведенные слова:\n"
      rec(head, 1, list)
    }
  }

  def getNothingMessage: String = "Ну и ладно\uD83E\uDD84"

}

object MarkupForReply {
  def getCommandMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup {
    Seq(
      Seq(
        InlineKeyboardButton(text = "Получить статистику\uD83D\uDC7B", callbackData = Some("stat")),
        InlineKeyboardButton(text = "Помощь\uD83D\uDCF1", callbackData = Some("help"))
      ),
      Seq(
        InlineKeyboardButton(text = "Ничего не нужно\uD83E\uDD95", callbackData = Some("nothing"))
      )
    )
  }

  def getTranslateMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup {
    Seq(
      Seq(
        InlineKeyboardButton(text = "Сохранить в недавние", callbackData = Some("save")),
        InlineKeyboardButton(text = "Не сохранять", callbackData = Some("not save"))
      )
    )
  }

}
