package on.insurance.supportbot.teligram


import on.insurance.supportbot.UserService
import on.insurance.supportbot.teligram.RoleService.RoleAdmin
import on.insurance.supportbot.teligram.RoleService.RoleOperator
import on.insurance.supportbot.teligram.RoleService.RoleUser
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


@Service
class BotService(
    val myBot: MyBot,
    val userService: UserService,
    val roleUser: RoleUser,
    val roleOperator: RoleOperator,
    val roleAdmin: RoleAdmin
) {

     fun massage(update: Update) {
        var chatId: Long = 0
        var text = ""
        update.run {
            chatId = message.chatId
            text = message.text
        }
        var user = userService.getUser(chatId)

        when(user.botStep){
            BotStep.START->{
                sendMassage(chatId,"tilni tanlang",languageButtons())
                user.botStep=BotStep.LANGUAGE
                userService.update(user)
            }
            BotStep.CONTACT->{
                sendMassage(chatId,"raxmat")
                user.botStep=BotStep.CHAT
                userService.update(user)
            }


        }

        when(user.role){
            Role.USER->{roleUser.userFunc(update,user)
            return}
            Role.OPERATOR ->{ roleOperator.operatorFunc(update, user)
            return}
            Role.ADMIN ->{ roleAdmin.adminFunc(update, user)
            return}
        }
    }

    fun inline(update: Update){
        var chatId: Long = 0
        var data = ""
        update.run {
            chatId=callbackQuery.message.chatId
            data=callbackQuery.data
        }
        var user = userService.getUser(chatId)
        var botStep = user.botStep

        when(botStep){

            BotStep.LANGUAGE->{
                myBot.deleteMassage(chatId,update)
                sendMassage(chatId,"kontakni yuboring")
                user.botStep=BotStep.CONTACT
                user.language= Language.valueOf(data)
                userService.update(user)
            }
        }
    }


    fun sendMassage(chatId: Long, text: String,inlineKeyboardMarkup: InlineKeyboardMarkup?) {
        val sendMessage = SendMessage(chatId.toString(), text)
        inlineKeyboardMarkup?.run { sendMessage.replyMarkup=this }
        sendMessage.enableMarkdown(true)
        myBot.execute(sendMessage) ?: throw TelegramApiException("xatolik")
    }

    fun sendMassage(chatId: Long, text: String) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.enableMarkdown(true)
        myBot.execute(sendMessage) ?: throw TelegramApiException("xatolik")
    }

    fun languageButtons():InlineKeyboardMarkup{
        val inlineKeyboardMarkup = InlineKeyboardMarkup()

        var keyboardButtons= mutableListOf<InlineKeyboardButton>()

        var buttons = listOf<Language>(Language.UZ, Language.RU, Language.ENG)
        buttons.forEach {
            val inlineKeyboardButton = InlineKeyboardButton()
            inlineKeyboardButton.text =it.name
            inlineKeyboardButton.callbackData = it.name
            keyboardButtons.add(inlineKeyboardButton);
        }
        val rowList: MutableList<List<InlineKeyboardButton>> = ArrayList()
        rowList.add(keyboardButtons)
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup
    }
}