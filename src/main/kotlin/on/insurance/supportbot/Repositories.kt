package on.insurance.supportbot

import antlr.collections.impl.LList
import on.insurance.supportbot.teligram.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import javax.persistence.EntityManager
import javax.transaction.Transactional

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun trash(id: Long): T
    fun trashList(ids: List<Long>): List<T>
    fun findByIdNotDeleted(id: Long): T?
    fun findAllNotDeleted(pageable: Pageable): Page<T>
    fun findAllNotDeleted(): List<T>
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {
    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    @Transactional
    override fun trash(id: Long) = save(findById(id).get().apply { deleted = true })
    override fun findAllNotDeleted(pageable: Pageable) = findAll(isNotDeletedSpecification, pageable)
    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun trashList(ids: List<Long>): List<T> = ids.map { trash(it) }
    override fun findByIdNotDeleted(id: Long): T? =
        findById(id).orElseGet { null }?.run { if (!this.deleted) this else null }
}


interface UserRepository : BaseRepository<User> {
    @Query("select * from users u where u.chat_id = ?1", nativeQuery = true)
    fun findByChatIdd(chatId: Long): User?

    @Query("select * from users u where u.role ='OPERATOR'", nativeQuery = true)
    fun getAllOperatorListByRole(): List<User>


    @Query(
        value = """select * from users u
    where u.deleted=false
     and u.is_active=true
     and u.role='OPERATOR' and u.language=:language limit 1""", nativeQuery = true
    )
    fun emptyOperator(language: String): User?
}

interface GroupRepository : BaseRepository<Group> {
    @Query("select * from groups g where g.user_id = ?1 and g.deleted = false ", nativeQuery = true)
    fun getGroupByUserIdAndActive(userId: Long): Group?

    @Query("select * from groups g where g.operator_id = ?1 and g.is_active = true", nativeQuery = true)
    fun getGroupByOperatorIdAndActive(operatorId: Long): Group?

    @Query(
        """select * from groups g where g.is_active=true and g.language=:language and
    g.operator_id is null and g.deleted = false order by created_date limit 1""", nativeQuery = true
    )
    fun getGroupByOperatorAndLanguageAndActive(language: String): Group?


    fun findByOperatorId(operatorId: Long): Group?

    //Operator_id buyicha barcha Grouplar
    @Query(
        """select DATE(g.created_date) kun,* from groups g
where operator_id=?1
  and created_date between ?2 and ?3 order by created_date""", nativeQuery = true
    )
    fun GroupsByOperatorId(operatorId: Long, first_day: String, last_day: String): List<GroupsByOperatorId>
}

interface ContactRepository : BaseRepository<Contact> {
    @Query(
        """select u.id as id, c.phone_number as telephoneNumber,c.user_name as fullName,
       u.language as systemLanguage, u.bot_step as state
from contact c
inner join users u on u.id = c.user_id
where u.deleted=false
""", nativeQuery = true
    )
    fun userInfo(pageable: Pageable): Page<ResponseUser>

    @Query(
        """select u.id as id, c.phone_number as telephoneNumber,c.user_name as fullName,
       u.language as systemLanguage, u.bot_step as state
from contact c
inner join users u on u.id = c.user_id
where u.deleted=false and u.id=:id
""", nativeQuery = true
    )
    fun getUser(id:Long): ResponseUser

    @Query("""select * from contact where user_id=:userId """, nativeQuery = true)
    fun findByUsername(userId:Long): Contact?

}

interface MessageRepository : BaseRepository<MessageEntity> {
    @Query(
        """select * from message m where m.readed=false and m.user_id=:userId and 
        m.group_id=:groupId order by created_date""", nativeQuery = true
    )
    fun getUserMessage(userId: Long, groupId: Long): List<MessageEntity>?

    @Query("""select * from message m where m.group_id=?1 order by created_date""", nativeQuery = true)
    fun getAllMessageByGroupId(groupId: Long): List<MessageEntity>
}

interface OperatorRepository : BaseRepository<Operator> {

    @Query("select (count(o) > 0) from Operator o where o.phoneNumber = ?1")
    fun existsByPhoneNumber(phoneNumber: String): Boolean

    @Query(
        """select * from operator where deleted=false""", nativeQuery = true
    )
    fun getAllOperator(): List<Operator>
}

interface AdminRepository : BaseRepository<Admin> {
    @Query("""select * from admin a where a.username=?1""", nativeQuery = true)
    fun findByUsername(username: String): Admin?
}

