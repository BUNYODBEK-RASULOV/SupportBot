package on.insurance.supportbot.teligram.RoleService

import on.insurance.supportbot.GroupService
import on.insurance.supportbot.MessageService
import on.insurance.supportbot.UserService
import on.insurance.supportbot.teligram.BotService
import on.insurance.supportbot.teligram.BotStep
import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.User
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

@Service
class RoleOperator(
    @Lazy
    val botService: BotService,
    val groupService: GroupService,
    val messageService: MessageService,
    val userService: UserService,
    @Lazy
    val roleUser: RoleUser,
) {
    lateinit var update: Update
    lateinit var operator: User
    lateinit var group: Group

    fun operatorFunc(updateFunc: Update,userFunc: User){
        update = updateFunc
        operator = userFunc
        group = groupService.getGroupByOperatorId(operator)

        scanButton(update.message.text)
        when (operator.botStep) {
            BotStep.CHAT -> {
                group.user?.run {
                    saveChat()
                    sendText() }
            }
            BotStep.BACK->{
                botService.sendMassage(update.message.chatId,"begin tugmasini bosing boshlash uchun",beginButton(""))
            }
        }
        userService.update(operator)

    }

    fun saveChat() {
        val text = update.message.text
        messageService.creat(text, group, operator,true)
    }

    fun sendText() {
        var chatId: Long = 0
        var text: String = ""
        update.message?.run {
            chatId = getChatId()
            text = getText()
        }
        group.user?.run { botService.sendMassage(this.chatId, text,roleUser.queueButton("")) }
    }



    fun scanButton(text:String){
        when(text){
            "yopish"->{
                operator.botStep=BotStep.CLOSE
            }
            "chiqish"->{
                operator.botStep=BotStep.BACK
            }
            "begin"->{
                operator.botStep=BotStep.CHAT
            }
        }
    }

    fun menuButton(lang: String): ReplyKeyboardMarkup = ReplyKeyboardMarkup().apply {
        oneTimeKeyboard = true
        resizeKeyboard = true
        selective = false
        keyboard = mutableListOf(KeyboardRow(listOf(
            KeyboardButton().apply {
                text = "yopish"
            },
            KeyboardButton().apply {
                text = "chiqish"
            }
        )))

    }

    fun beginButton(lang: String): ReplyKeyboardMarkup = ReplyKeyboardMarkup().apply {
        oneTimeKeyboard = true
        resizeKeyboard = true
        selective = false
        keyboard = mutableListOf(KeyboardRow(listOf(
            KeyboardButton().apply {
                text = "begin"
            }
        )))

    }
}