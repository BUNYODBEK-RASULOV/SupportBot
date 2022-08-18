package on.insurance.supportbot

import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.User
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service

interface UserService{
    fun getUser(chatId:Long):User
    fun update(user: User)
    fun get(userId:Long): Group
}
interface GroupService {

    fun update(user: User): Group
    fun getUser(userId: Long): Group

}



@Service
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) : GroupService {
    override fun update(user: User): Group {
        TODO("Not yet implemented")
    }

    override fun getUser(userId: Long): Group {
        return groupRepository.findByUserIdAndDeleted(userId).run { this }.createUser(userId)
    }

    fun Group.createUser(userId: Long): Group {
        return groupRepository.save(Group(userRepository.findById(userId).get()))
    }


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
):UserService{
    override fun getUser(chatId: Long): User {
        return userRepository.findByChatId(chatId).run { this }.createUser(chatId)
    }

    override fun update(user: User) {
        userRepository.save(user)
    }

    override fun get(userId: Long): Group {
        TODO("Not yet implemented")
    }

}
}
