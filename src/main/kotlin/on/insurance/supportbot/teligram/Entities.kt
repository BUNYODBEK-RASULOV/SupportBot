package on.insurance.supportbot.teligram

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)

@Entity
class Contact(
    @Column(unique = true, nullable = false) var phoneNumber: String,
   @OneToOne var userId: User,
    var userName: String
) : BaseEntity()

@Entity
class User(
    var chatId:Long,
    @Enumerated(EnumType.STRING) var botStep: BotStep=BotStep.START,
    @Enumerated(EnumType.STRING) var language:Language= Language.UZ,
    @Enumerated(EnumType.STRING) var role:Role = Role.USER,
    var isActive:Boolean= true
) : BaseEntity()

@Entity
class Messege(
    var chatId:Long,
    @Enumerated(EnumType.STRING) var botStep: BotStep,
    @Enumerated(EnumType.STRING) var language:Language,
    @Enumerated(EnumType.STRING) var role:Role,
    var isActive:Boolean= true
) : BaseEntity()
