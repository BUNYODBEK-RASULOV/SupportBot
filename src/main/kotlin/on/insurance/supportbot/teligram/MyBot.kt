package on.insurance.supportbot.teligram

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ForwardMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


@Service
class MyBot(
    @Lazy
    val botService: BotService
) : TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update) {
        update.callbackQuery?.run { botService.inline(update) }
        update.message?.run { botService.massage(update) }
    }

    fun deleteMassage(chatId: Long, update: Update) {
        var chatId: Long = 0
        var messageId: Int = 1
        update.callbackQuery?.run {
            chatId = message.chatId
            messageId = message.messageId
        }
        update.message?.run {
            chatId = getChatId()
            messageId = getMessageId()
        }

        val deleteMessage = DeleteMessage(chatId.toString(), messageId)
        try {
            execute(deleteMessage)
        } catch (tae: TelegramApiException) {
            throw RuntimeException(tae)
        }
    }

    fun forwardMessage(update: Update,chat_id:Long){
        var from_chat_id: Long = 0
        var message_id: Int = 1
        update.callbackQuery?.run {
            from_chat_id = message.chatId
            message_id = message.messageId
        }
        update.message?.run {
            from_chat_id =chatId
            message_id = messageId
        }
        val  forwardMessage=ForwardMessage(chat_id.toString(),from_chat_id.toString(),message_id)
        try {
            execute(forwardMessage)
        } catch (tae: TelegramApiException) {
            throw RuntimeException(tae)
        }
    }
    fun forwardMessage(chat_id:Long,from_chat_id: Long,message_id: Int){
        val  forwardMessage=ForwardMessage(chat_id.toString(),from_chat_id.toString(),message_id)
        try {
            execute(forwardMessage)
        } catch (tae: TelegramApiException) {
            throw RuntimeException(tae)
        }
    }


}
