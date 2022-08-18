package on.insurance.supportbot

import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service

interface UserService{
    fun getUser(chatId:Long):User
    fun update(user: User)
    fun get(userId:Long): Group
}
interface GroupService {

    fun update(group: Group): Group
    fun getGroupByUserId(userId: Long): Group

}



//@Service
//class GroupServiceImpl(
//    val groupRepository: GroupRepository,
//    val userRepository: UserRepository,
//) : GroupService {

//    override fun update(group: Group): Group {
//        return groupRepository.save(group)
//    }
//
//    override fun getGroupByUserId(userId: Long): Group {
//        return groupRepository.findByUserIdAndDeleted(userId).run { this }.createGroup(userId)
//    }
//
//    fun Group.createGroup(userId: Long): Group {
//        return groupRepository.save(Group(userRepository.findById(userId).get()))
//    }


@Service
class UserServiceImpl(
    private val userRepository: UserRepository
):UserService{
    override fun getUser(chatId: Long): User {
        return userRepository.findByChatIdd(chatId)?.run { this }?:createUser(chatId)
    }


    fun createUser(chatId:Long): User {
        return userRepository.save(User(chatId))
    }
    override fun update(user: User) {
        userRepository.save(user)
    }

    override fun get(userId: Long): Group {
        TODO("Not yet implemented")
    }

}

fun main() {


}

