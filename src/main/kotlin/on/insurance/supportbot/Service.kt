package on.insurance.supportbot

import on.insurance.supportbot.teligram.*
import on.insurance.supportbot.teligram.Contact
import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.User
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

interface UserService {
    fun getUser(chatId: Long): User
    fun update(user: User)
    fun get(userId: Long): Group
    fun backOperator(operator: User)
    fun operatorIsActive(operator: User)
}

interface GroupService {
    fun update(group: Group): Group
    fun getGroupByUserId(user: User): Group
    fun getNewGroupByOperator(operator: User):Group?
    fun getGroupByOperatorId(operator:User): Group?

    // groupni yopish
    fun deleteGroupByOperator(operator: User)

}
interface MessageService{
    fun creat(message: String,group: Group,user: User)
    fun creat(message: String,group: Group,user: User,readed:Boolean)
    fun getUserMessage(group: Group):List<MessageEntity>?
}

interface ContactService {
    fun saveContact(phoneNumber: String, username: String, user: User)
    fun checkContact(contact: Contact, user: User)
}

interface OperatorService {
    fun create(dto: OperatorCreateDto)
    fun update(id: Long, dto: OperatorUpdateDto)
    fun get(id: Long): OperatorDto
    fun delete(id: Long)
    fun listOfOperator(): List<OperatorDto>
}

@Service
class MessageServiceImpl(
    val messageRepository: MessageRepository
) : MessageService {
    override fun creat(message: String, group: Group, user: User) {
        messageRepository.save(MessageEntity(user, group, message, user.language))
    }

    override fun creat(message: String, group: Group, user: User, readed: Boolean) {
        messageRepository.save(MessageEntity(user, group, message, user.language, readed))
    }

    override fun getUserMessage(group: Group): List<MessageEntity>{
            messageRepository.getUserMessage(group.user!!.id!!, group.id!!)?.run {
                val list= mutableListOf<MessageEntity>()
                for (entity in this){
                    entity.readed=true
                    list.add(entity)
                }
                messageRepository.saveAll(list)
                return list
            }
        return emptyList()
    }
}
@Service
class GroupServiceImpl(
    val groupRepository: GroupRepository,
    val userRepository: UserRepository,
) : GroupService {

    override fun update(group: Group): Group {
//        groupRepository.findById(group)
        return groupRepository.save(group)
    }

    override fun getGroupByUserId(user: User): Group {
    return groupRepository.getGroupByUserIdAndActive(user.id!!).run { this } ?: createGroup(user)
    }


    fun createGroup(user: User): Group {
        return groupRepository.save(Group(user,null,user.language))
    }
    override fun getGroupByOperatorId(operator: User): Group {
        return groupRepository.getGroupByOperatorIdAndActive(operator.id!!)?.run { this } ?: Group(null,null,null)
    }


    override fun getNewGroupByOperator(operator: User): Group? {
       return  groupRepository.getGroupByOperatorAndLanguageAndActive(operator.language.name)?:Group(null,null,null)
    }

    override fun deleteGroupByOperator(operator: User) {
        groupRepository.existsByActiveAndOperatorId(operator.id!!)
            .ifTrue { groupRepository.deleteGroup(operator.id!!) }

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

    override fun backOperator(operator: User) {
        operator.isActive=false
        userRepository.save(operator)
    }

    override fun operatorIsActive(operator: User) {
        operator.isActive=true
        userRepository.save(operator)
    }
}

@Service
class ContactServiceImpl(private val contactRepository: ContactRepository):ContactService{
    override fun saveContact(phoneNumber: String, username: String, user: User) {
            var contact= Contact(phoneNumber,user,username)
             contact=contactRepository.save(contact)
    }

    override fun checkContact(contact: Contact, user: User) {

    }
}

@Service
class OperatorServiceImpl(
    private val repository: OperatorRepository
) : OperatorService {
    override fun create(dto: OperatorCreateDto) {
        repository.save(dto.toEntity())
    }

    override fun update(id: Long, dto: OperatorUpdateDto) {
        val entity = repository.findByIdNotDeleted(id) ?: throw NullPointerException("we have not this operator")
        dto.run {
            name?.run { entity.name = this }
            phoneNumber?.run { entity.phoneNumber = this }
            repository.save(entity)
        }
    }

    override fun get(id: Long): OperatorDto = repository.findByIdNotDeleted(id)?.run { OperatorDto.toDto(this) }
        ?: throw NullPointerException("Couldn't find by id")


    override fun delete(id: Long) {
        repository.trash(id)
    }

    override fun listOfOperator() = repository.getAllOperator().map(OperatorDto.Companion::toDto)
}






