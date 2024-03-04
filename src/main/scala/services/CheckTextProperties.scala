package services

object CheckTextProperties {
  def checkOnCommand(text: String): Boolean = {
    text.startsWith("/")
  }
}
