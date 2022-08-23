package on.insurance.supportbot

import on.insurance.supportbot.teligram.*
import on.insurance.supportbot.teligram.Contact
import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import javax.persistence.EntityManager
import kotlin.NullPointerException

interface UserService {
    fun getUser(chatId: Long): User
    fun update(user: User)
    fun get(userId: Long): Group
    fun backOperator(operator: User)
    fun operatorIsActive(operator: User)
    fun emptyOperator(user: User): User?
    fun checkOperator(contact: Contact, user: User): User
    fun operatorList():List<User>
}

interface GroupService {
    fun update(group: Group): Group
    fun getGroupByUserId(user: User): Group
    fun getNewGroupByOperator(operator: User): Group?
    fun getGroupByOperatorId(operator: User): Group?

    //operator_id buyicha groupList admin uchun
//    operator_id, first_day, last_day kirib keladi
    fun groupsByOperatorId(groupsByOperatorIdDto: GroupsByOperatorIdDto): List<GroupsByOperatorId>

}

interface MessageService{
    fun creat(update: Update,group: Group,user: User)
    fun creat(update: Update,group: Group,user: User,readed:Boolean)
    fun getUserMessage(group: Group):List<MessageEntity>
    //messagelar Listini group id buyicha olish
    fun getAllMessageByGroupId(groupId:Long):List<MessageEntity>
}

interface ContactService {
    fun saveContact(phoneNumber: String, username: String, user: User): Contact
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
    override fun creat(update: Update, group: Group, user: User) {
        var chatId:Long=1
        var massageId:Int=0
        var massage:String=""
        update.message?.run {
            chatId=this.chatId
            massageId=this.messageId
            massage=this.text
        }

        messageRepository.save(MessageEntity(chatId,massageId,user, group, massage, user.language))
    }

    override fun creat(update: Update, group: Group, user: User, readed: Boolean) {
        var chatId:Long=1
        var massageId:Int=0
        var massage:String=""
        update.message?.run {
            chatId=this.chatId
            massageId=this.messageId
            massage=this.text
        }
        messageRepository.save(MessageEntity(chatId,massageId,user, group, massage, user.language, readed))
    }

    override fun getUserMessage(group: Group): List<MessageEntity> {
        messageRepository.getUserMessage(group.user!!.id!!, group.id!!)?.run {
            val list = mutableListOf<MessageEntity>()
            for (entity in this) {
                entity.readed = true
                list.add(entity)
            }
            messageRepository.saveAll(list)
            return list
        }
        return emptyList()
    }

    override fun getAllMessageByGroupId(groupId: Long): List<MessageEntity> {
      return  messageRepository.getAllMessageByGroupId(groupId)
    }
}

@Service
class GroupServiceImpl(
    val groupRepository: GroupRepository,
    val userService: UserService,
) : GroupService {

    override fun update(group: Group): Group {
        return groupRepository.save(group)
    }

    override fun getGroupByUserId(user: User): Group {
        return groupRepository.getGroupByUserIdAndActive(user.id!!).run { this } ?: createGroup(user)
    }


    fun createGroup(user: User): Group {
        val emptyOperator = userService.emptyOperator(user)
        emptyOperator?.run { userService.backOperator(this) }
        return groupRepository.save(Group(user, emptyOperator, user.language))
    }


    override fun getGroupByOperatorId(operator: User): Group? {
        return groupRepository.getGroupByOperatorIdAndActive(operator.id!!)?.run { this }
    }


    override fun getNewGroupByOperator(operator: User): Group? {
        return groupRepository.getGroupByOperatorAndLanguageAndActive(operator.language.name)?.run { this }
    }

    override fun groupsByOperatorId(dto: GroupsByOperatorIdDto): List<GroupsByOperatorId> {
        return groupRepository.GroupsByOperatorId(dto.operator_id,dto.first_day,dto.last_day)
    }
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val operatorRepository: OperatorRepository
) : UserService {
    override fun getUser(chatId: Long): User {
        return userRepository.findByChatIdd(chatId)?.run { this } ?: createUser(chatId)
    }

    override fun emptyOperator(user: User): User? {
        return userRepository.emptyOperator(user.language.name)
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
        operator.isActive = false
        userRepository.save(operator)
    }

    override fun operatorIsActive(operator: User) {
        operator.isActive = true
        userRepository.save(operator)
    }

    override fun checkOperator(contact: Contact, user: User): User {
        val phoneNumber = contact.phoneNumber
        if (operatorRepository.existsByPhoneNumber(phoneNumber)) {
            user.run { this.role = Role.OPERATOR }
        } else {
            user.role = Role.USER
        }
        return user
    }

    override fun operatorList(): List<User>{
      return  userRepository.getAllOperatorListByRole()
    }
}

@Service
class ContactServiceImpl(private val contactRepository: ContactRepository) : ContactService {
    override fun saveContact(phoneNumber: String, username: String, user: User): Contact {
        val contact = Contact(phoneNumber, user, username)
        return contactRepository.save(contact)
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

@Service
class AuthService(
    private val adminRepository: AdminRepository
):UserDetailsService{

    @Throws(NullPointerException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val admin = adminRepository.findByUsername(username)?:throw NullPointerException("Invalid username or password")
        return admin
    }

}






