package on.insurance.supportbot.teligram

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.ws.rs.ext.ParamConverter

@Service
class MyBot(
    @Lazy
    val botService: BotService
): TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private val botName: String=""

    @Value("\${telegram.token}")
    private val token: String = ""

    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update) {
        update.message?.run { botService.sendMassage(chatId,text) }
    }


}
