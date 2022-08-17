package on.insurance.supportbot.teligram


import on.insurance.supportbot.UserService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class BotService(
    val myBot: MyBot,
    val userService: UserService
) {

    public fun massage(update: Update) {
        var chatId: Long = 0
        var text = ""
        update.run {
            chatId = message.chatId
            text = message.text
        }
        var user = userService.getUser(chatId)
        var botStep = BotStep.START

    }


    fun sendMassage(chatId: Long, text: String) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdown(true)
        myBot.execute(sendMessage) ?: throw TelegramApiException("xatolik")
    }
}