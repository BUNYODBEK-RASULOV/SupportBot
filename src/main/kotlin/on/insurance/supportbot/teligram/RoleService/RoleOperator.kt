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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

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

        group = groupService.getGroupByOperatorId(operator)?.run {  this }!!
        update.message?.text?.run { scanButton(this) }
        when (operator.botStep) {
            BotStep.CHAT -> {
                var user:User?=group.user
                if (user!=null){
                    saveChat()
                    sendText()
                }
            }
            BotStep.BACK->{
                botService.sendMassage(update.message.chatId,"begin tugmasini bosing boshlash uchun",beginButton(""))
            }
            BotStep.BEGIN->{
                botService.sendMassage(update.message.chatId,"Siz activ holga utdingiz",menuButton(""))
                operator.botStep=BotStep.CHAT
                begin()
            }
            BotStep.CLOSE->{
                botService.sendMassage(update.message.chatId,"Chat yangilandi",menuButton(""))
                operator.botStep=BotStep.CHAT
                begin()
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
                groupService.deleteGroupByOperator(operator)
            }
            "chiqish"->{
                operator.botStep=BotStep.BACK
                userService.backOperator(operator)
                groupService.deleteGroupByOperator(operator)
            }
            "begin"->{
                operator.botStep=BotStep.BEGIN
                userService.operatorIsActive(operator)
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

    fun begin(){
        var opChatId=operator.chatId
        var operator1=operator
        groupService.getNewGroupByOperator(operator1)?.run {
            val group1=this
            println(group1.toString())
                messageService.getUserMessage(group1)?.run {
                    val userMessage =this
                    userMessage.forEach {
                        botService.sendMassage(opChatId,it.massages)
                    }
                    group1.operator=operator1
                    group1.isActive=true
                    groupService.update(group1)
                }
        }

    }
}