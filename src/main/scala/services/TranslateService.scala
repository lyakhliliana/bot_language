package services

import cats.effect.IO
import cats.effect.IO.pure
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.TranslateOptions

class TranslateService {
  private val translateService = pure(TranslateOptions.newBuilder.build.getService)
  def translate(word: String): IO[String] = {
    val translation_func =
      translateService.map(
        _.translate(word, TranslateOption.sourceLanguage("ru"), TranslateOption.targetLanguage("en")).getTranslatedText
      )
    translation_func
  }

}

object TranslateService {
  def apply(): TranslateService = new TranslateService()
}
