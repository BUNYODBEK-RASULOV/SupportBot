package on.insurance.supportbot.teligram

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class MyBot(): TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private val botName: String=""

    @Value("\${telegram.token}")
    private val token: String = ""

    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update?) {
        update!!.hasMessage()?.run { sendNotification(update.message.chatId,"salom") }

    }

    private fun sendNotification(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)
        responseMessage.enableMarkdown(true)
        execute(responseMessage)
    }
}
