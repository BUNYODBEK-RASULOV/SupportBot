package on.insurance.supportbot

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
import org.telegram.telegrambots.meta.api.objects.Message
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
    @Query("select * from groups g where g.user_id = ?1 and g.deleted = false", nativeQuery = true)
    fun  findByUserIdAndDeleted(userId:Long): Group?
}
interface ContactRepository:BaseRepository<Contact>{
}

interface MessageRepository:BaseRepository<MessageEntity>{

}