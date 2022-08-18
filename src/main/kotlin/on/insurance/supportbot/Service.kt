package on.insurance.supportbot

import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.Language
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Query
import org.springframework.expression.spel.ast.Operator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

interface UserService {
    fun getUser(chatId: Long): User
    fun update(user: User)
    fun get(userId: Long): Group
}

interface GroupService {
    fun update(group: Group): Group
    fun getGroupByUserId(user: User): Group
    fun connectOperator(operator: User):Group?
    fun getGroupByOperatorId(operator:User): Group

//    fun connectOperator(operator: User):Group
//    operator= null
//    and isActive = true
//    and group.Language=operator.Language
//    order by date 1

}
interface MessageService{
    fun creat(message: String,group: Group,user: User)
    fun creat(message: String,group: Group,user: User,readed:Boolean)
    fun getUserMessage(user: User,group: Group):List<MessageEntity>
//    order date, readed=false,
//    kiyin readed=true qilib quyasizlar
}

@Service
 class MessageServiceImpl(
    val messageRepository:MessageRepository
    ):MessageService{
    override fun creat(message: String, group: Group, user: User) {
        messageRepository.save(MessageEntity(user,group,message,user.language))
    }

    override fun creat(message: String, group: Group, user: User, readed: Boolean) {
        messageRepository.save(MessageEntity(user,group,message,user.language,readed))
    }

    override fun getUserMessage(user: User, group: Group): List<MessageEntity> {
        val userId=user.id
        val groupId=group.id
        val messageEntityList = messageRepository.getUserMessage(userId!!, groupId!!)
        val list= mutableListOf<MessageEntity>()
        for (entity in messageEntityList){
            entity.readed=true
            list.add(entity)
        }
        messageRepository.saveAll(list)
        return list
    }
}
@Service
class GroupServiceImpl(
    val groupRepository: GroupRepository,
    val userRepository: UserRepository,
) : GroupService {

    override fun update(group: Group): Group {
        return groupRepository.save(group)
    }

    override fun getGroupByUserId(user: User): Group {
    return groupRepository.findByUserIdAndDeleted(user.id!!).run { this } ?: createGroup(user)
    }


    fun createGroup(user: User): Group {
        return groupRepository.save(Group(user,null,user.language))
    }
    override fun getGroupByOperatorId(operator: User): Group {
        return groupRepository.findByOperatorIdAndDeleted(operator.id!!).run { this } ?: Group(  )
    }


    override fun connectOperator(operator: User): Group? {
       return  groupRepository.getOperator(operator.language)?:throw RuntimeException("bunday group yoq")
    }

}
@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun getUser(chatId: Long): User {
        return userRepository.findByChatIdd(chatId)?.run { this } ?: createUser(chatId)
    }


    fun createUser(chatId: Long): User {
        return userRepository.save(User(chatId))
    }

    override fun update(user: User) {
        userRepository.save(user)
    }

    override fun get(userId: Long): Group {
        TODO("Not yet implemented")
    }

}



