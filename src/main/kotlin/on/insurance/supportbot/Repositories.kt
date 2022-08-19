package on.insurance.supportbot

import on.insurance.supportbot.teligram.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
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
        @Query("select * from users u where u.chat_id = ?1",nativeQuery = true)
        fun findByChatIdd(chatId:Long):User?
}
interface GroupRepository : BaseRepository<Group>{
    @Query("select * from groups g where g.user_id = ?1 and g.is_active = true ", nativeQuery = true)
    fun  getGroupByUserIdAndActive(userId:Long): Group?

    @Query("select * from groups g where g.operator_id = ?1 and g.is_active = true", nativeQuery = true)
    fun  getGroupByOperatorIdAndActive(operatorId:Long): Group?

    @Query("""select * from groups g where g.is_active=true and g.language=:language and
    g.operator_id is null and g.deleted = false and g.user_id!=:operatorId order by created_date limit 1""", nativeQuery = true)
    fun  getGroupByOperatorAndLanguageAndActive(language: String,operatorId:Long): Group?

    @Query("""update  groups g  set is_active=false where g.operator_id=?1 and is_active=true""", nativeQuery = true)
    fun deleteGroup(operatorId: Long):Group?

    @Query(value = "select (count(g) > 0) from groups g where g.is_active = true and g.operator_id = ?1",nativeQuery = true)
    fun existsByActiveAndOperatorId(operatorId:Long):Boolean

    fun findByOperatorId(operatorId: Long):Group?
}
interface ContactRepository:BaseRepository<Contact>{

}

interface MessageRepository:BaseRepository<MessageEntity>{
    @Query("""select * from message m where m.readed=false and m.user_id=:userId and 
        m.group_id=:groupId order by created_date""", nativeQuery = true)
    fun getUserMessage(userId:Long,groupId:Long):List<MessageEntity>
}