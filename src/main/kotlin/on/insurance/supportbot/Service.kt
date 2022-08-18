package on.insurance.supportbot

import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.RoleService.*
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
    fun connectOperator(operator: User): Group?


}

interface OperatorService {
    fun create(dto: OperatorCreateDto)
    fun update(id: Long, dto: OperatorUpdateDto)
    fun get(id: Long): OperatorDto
    fun delete(id: Long)
    fun listOfOperator(): List<OperatorDto>

}

interface MessageService {
    fun creat(message: String, group: Group, user: User)
}


@Service
class MessageServiceImpl(
    val messageRepository: MessageRepository
) : MessageService {
    override fun creat(message: String, group: Group, user: User) {
        messageRepository.save(MessageEntity(user, group, message, user.language))
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
        return groupRepository.findByOperatorIdAndDeleted(operatorId).run { this } ?: Group()
    }


    override fun connectOperator(operator: User): Group? {
        return groupRepository.getOperator(operator.language) ?: throw RuntimeException("bunday group yoq")
    }

    fun createGroup(userId: Long): Group {
        return groupRepository.save(Group(userRepository.findById(userId).get()))
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

@Service
class OperatorServiceImpl(private val repository: OperatorRepository) : OperatorService {
    override fun create(dto: OperatorCreateDto) {
        repository.save(dto.toEntity())
    }

    override fun update(id: Long, dto: OperatorUpdateDto) {
        val entity = repository.findByIdNotDeleted(id) ?:
        throw NullPointerException("we have not this operator")
        dto.run {
            name?.run { entity.name = this }
            phoneNumber?.run { entity.phoneNumber = this }
            repository.save(entity)
        }
    }

    override fun get(id: Long): OperatorDto =  repository.findByIdNotDeleted(id)?.run { OperatorDto.toDto(this) }
        ?: throw NullPointerException("Couldn't find by id")


    override fun delete(id: Long) {
        repository.trash(id)
    }

    override fun listOfOperator()= repository.findAllNotDeleted().map(OperatorDto.Companion::toDto)



}