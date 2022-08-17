package on.insurance.supportbot.teligram


import com.sun.org.apache.bcel.internal.generic.SWITCH
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class BotService(
    val myBot: MyBot,
) {

    public fun massage(update: Update){
        update.run {
            val chatId=message.chatId
            val text=message.text
        }
        var botStep=BotStep.START


    }




    fun sendMassage(chatId: Long, text: String) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdown(true)
         myBot.execute(sendMessage)?:throw TelegramApiException("xatolik")
    }
}