package on.insurance.supportbot

import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.User
import org.springframework.stereotype.Service

interface UserService {
    fun getUser(chatId: Long): User
    fun update(user: User)
    fun get(userId: Long): Group
}

interface GroupService {
    fun update(group: Group): Group
    fun getGroupByUserId(userId: Long): Group
    fun getGroupByOperatorId(operatorId: Long): Group
    fun connectOperator(operator: User):Group?


}
interface MessageService{
    fun creat(message: String,group: Group,user: User)
}

@Service
 class MessageServiceImpl(
    val messageRepository:MessageRepository
    ):MessageService{
    override fun creat(message: String, group: Group, user: User) {
        messageRepository.save(MessageEntity(user,group,message,user.language))
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
    override fun getGroupByUserId(userId: Long): Group {
    return groupRepository.findByUserIdAndDeleted(userId).run { this } ?: createGroup(userId)
    }
    override fun getGroupByOperatorId(operatorId: Long): Group {
        return groupRepository.findByOperatorIdAndDeleted(operatorId).run { this } ?: Group(  )
    }


    override fun connectOperator(operator: User): Group? {
       return  groupRepository.getOperator(operator.language)?:throw RuntimeException("bunday group yoq")
    }

    fun createGroup(userId: Long): Group {
        return groupRepository.save(Group(userRepository.findById(userId).get()))
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
}
