package on.insurance.supportbot.teligram.RoleService

import on.insurance.supportbot.GroupService
import on.insurance.supportbot.MessageService
import on.insurance.supportbot.teligram.BotService
import on.insurance.supportbot.teligram.BotStep
import on.insurance.supportbot.teligram.Role
import on.insurance.supportbot.teligram.User
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class RoleUser(
    @Lazy
    val botService: BotService,
    val groupService: GroupService,
    var messageService:MessageService
) {
    lateinit var update: Update
    lateinit var user: User

    fun userFunc(updateFunc: Update,userFunc: User){
        update=updateFunc
        user=userFunc

        when(user.botStep){
            BotStep.CHAT->{
                chat()
            }
        }
    }

    fun chat(){
        val group=groupService.getGroupByUserId(user.id!!)
        val text=update.message.text
        messageService.creat(text,group,user)
    }
}