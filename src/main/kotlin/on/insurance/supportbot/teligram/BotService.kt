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
        println(chatId)
        var user = userService.getUser(chatId)
        var botStep = user.botStep

        when(botStep){
            BotStep.START->{
                sendMassage(chatId,"tilni tanlang")
                user.botStep=BotStep.LANGUAGE
                userService.update(user)
            }
            BotStep.LANGUAGE->{
                sendMassage(chatId,"kontakni yuboring")
                user.botStep=BotStep.CONTACT
                userService.update(user)
            }
            BotStep.CONTACT->{
                sendMassage(chatId,"raxmat")
            }
        }

    }


    fun sendMassage(chatId: Long, text: String) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdown(true)
        myBot.execute(sendMessage) ?: throw TelegramApiException("xatolik")
    }
}