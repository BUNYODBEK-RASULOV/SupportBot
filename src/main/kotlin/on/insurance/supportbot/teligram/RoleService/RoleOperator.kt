package on.insurance.supportbot.teligram.RoleService

import on.insurance.supportbot.GroupService
import on.insurance.supportbot.MessageService
import on.insurance.supportbot.UserService
import on.insurance.supportbot.teligram.*
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
    val myBot: MyBot,
    @Lazy
    val roleUser: RoleUser,
) {
    lateinit var update: Update
    lateinit var operator: User
    var group: Group?=null

    fun operatorFunc(updateFunc: Update,userFunc: User){
        update = updateFunc
        operator = userFunc

        group = groupService.getGroupByOperatorId(operator)?.run {  this }
        update.message?.text?.run { scanButton(this) }
        when (operator.botStep) {
            BotStep.CHAT -> {
                var user:User?=group?.user
                if (user!=null){
                    saveChat()
                    sendText()
                }
            }
            BotStep.BACK->{
                botService.sendMassage(update.message.chatId,"begin tugmasini bosing boshlash uchun",beginButton(""))
                userService.backOperator(operator)
                group?.run {
                    group!!.isActive=false
                    groupService.update(group!!)
                }
            }
            BotStep.BEGIN->{
                botService.sendMassage(update.message.chatId,"Siz activ holga utdingiz",menuButton(""))
                userService.operatorIsActive(operator)
                operator.botStep=BotStep.CHAT
                begin()
            }
            BotStep.CLOSE->{
                botService.sendMassage(update.message.chatId,"Chat yangilandi",menuButton(""))
                userService.operatorIsActive(operator)
                operator.botStep=BotStep.CHAT
                begin()
            }
        }
        userService.update(operator)

    }

    fun saveChat() {
        messageService.creat(update, group!!, operator,true)
    }

    fun sendText() {
        var chatId: Long = 0
        var text: String = ""
        update.message?.run {
            chatId = getChatId()
            text = getText()
        }
        group!!.user?.run { botService.sendMassage(this.chatId, text,) }
    }

    fun scanButton(text:String){

        when(text){
            "yopish"->{
                operator.botStep=BotStep.CLOSE
                group?.run {
                    group!!.isActive=false
                    groupService.update(group!!)
                }
            }
            "chiqish"->{
                operator.botStep=BotStep.BACK
            }
            "begin"->{
                operator.botStep=BotStep.BEGIN
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
            userService.backOperator(operator1)
            val group1=this
                messageService.getUserMessage(group1)?.run {
                    val userMessage =this
                    userMessage.forEach {
                        myBot.forwardMessage(opChatId,it.chatId,it.massageId)
//                        botService.sendMassage(opChatId,it.massages)
                    }
                    group1.operator=operator1
                    group1.isActive=true
                    groupService.update(group1)
                }
        }

    }
}