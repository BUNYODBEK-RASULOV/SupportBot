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
) {
    lateinit var update: Update
    lateinit var operator: User
    lateinit var group: Group

    fun operatorFunc(updateFunc: Update,userFunc: User){
        update = updateFunc
        operator = userFunc
        //getGroupByOperatorId(operator:User) kerak
        group = groupService.getGroupByOperatorId(operator)

        when (operator.botStep) {
            BotStep.CHAT -> {
                group.user?.run {
                    saveChat()
                    sendText() }
            }
        }
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
        group.user?.run { botService.sendMassage(this.chatId, text,queueButton("")) }
    }

    fun queueButton(lang: String): ReplyKeyboardMarkup = ReplyKeyboardMarkup().apply {
        oneTimeKeyboard = true
        resizeKeyboard = true
        selective = false
        keyboard = mutableListOf(KeyboardRow(listOf(
            KeyboardButton().apply {
                text = "navbatingizni bilish"
                requestContact = false
            }
        )))

    }
}