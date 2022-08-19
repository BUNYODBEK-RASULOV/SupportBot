package on.insurance.supportbot.teligram


import on.insurance.supportbot.ContactService
import on.insurance.supportbot.UserService
import on.insurance.supportbot.teligram.RoleService.RoleAdmin
import on.insurance.supportbot.teligram.RoleService.RoleOperator
import on.insurance.supportbot.teligram.RoleService.RoleUser
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


@Service
class BotService(
    val myBot: MyBot,
    val userService: UserService,
    val roleUser: RoleUser,
    val roleOperator: RoleOperator,
    val roleAdmin: RoleAdmin,
    val contactService: ContactService,

) {
    lateinit var operator: User

     fun massage(update: Update) {
        var chatId: Long = 0
        update.run {
            chatId = message.chatId
        }
        var user = userService.getUser(chatId)

        when(user.botStep){
            BotStep.START->{
                sendMassage(chatId,"tilni tanlang",languageButtons())
                user.botStep=BotStep.LANGUAGE
                userService.update(user)
            }
            BotStep.CONTACT->{
                val contact = update.message.contact
                contactService.saveContact(contact.phoneNumber,contact.firstName,user)
                contactService.checkContact(contact.phoneNumber,user)
                sendMassage(chatId,"raxmat")
                user.botStep=BotStep.QUEUE
                userService.update(user)
            }
            BotStep.BEGIN->{
                sendMassage(update.message.chatId,"Siz activ holga utdingiz",roleOperator.menuButton(""))
                operator.botStep=BotStep.CHAT
               roleOperator.begin()
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
                val contact = update.message.contact
                myBot.deleteMassage(chatId,update)
                sendMassage(chatId,"contactizni yuboring",getContact(""))
             //   contactService.checkContact(contact.phoneNumber,user)
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

    fun sendMassage(chatId: Long, text: String,replyKeyboardMarkup: ReplyKeyboardMarkup) {
        val sendMessage = SendMessage(chatId.toString(), text)
        sendMessage.replyMarkup=replyKeyboardMarkup
        sendMessage.enableMarkdown(true)
        myBot.execute(sendMessage) ?: throw TelegramApiException("xatolik")
    }

    fun languageButtons():InlineKeyboardMarkup{
        val inlineKeyboardMarkup = InlineKeyboardMarkup()
        val keyboardButtons= mutableListOf<InlineKeyboardButton>()
        val buttons = listOf<Language>(Language.UZ, Language.RU, Language.ENG)
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

    fun getContact(lang: String): ReplyKeyboardMarkup = ReplyKeyboardMarkup().apply {
        oneTimeKeyboard = true
        resizeKeyboard = true
        selective = false
        keyboard = mutableListOf(KeyboardRow(listOf(
            KeyboardButton().apply {
                text = "share contact"
                requestContact = true
            }
        )))

    }
}