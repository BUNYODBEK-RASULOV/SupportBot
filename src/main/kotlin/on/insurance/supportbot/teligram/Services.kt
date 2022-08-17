package on.insurance.supportbot.teligram

import org.springframework.stereotype.Service

class Services {

    interface UserService{
     fun create(user: User)
     fun update(user: User)
     fun getUser(userId: Long):User
     fun getUserList():List<User>
    }
    @Service
    class UserServiceImpl( ):UserService{
        override fun create(user: User) {
            TODO("Not yet implemented")
        }

        override fun update(user: User) {
            TODO("Not yet implemented")
        }

        override fun getUser(userId: Long): User {
            TODO("Not yet implemented")
        }

        override fun getUserList(): List<User> {
            TODO("Not yet implemented")
        }


    }
}