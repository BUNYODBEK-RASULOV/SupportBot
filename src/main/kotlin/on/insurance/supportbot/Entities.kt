package on.insurance.supportbot.teligram

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.awt.TrayIcon
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
    @OneToOne var user: User,
    var userName: String
) : BaseEntity()

@Entity
@Table(name = "users")
class User(
    var chatId: Long,
    @Enumerated(EnumType.STRING) var botStep: BotStep = BotStep.START,
    @Enumerated(EnumType.STRING) var language: Language = Language.UZ,
    @Enumerated(EnumType.STRING) var role: Role? = null,
    var isActive: Boolean = true
) : BaseEntity()

@Entity(name = "message")
data class MessageEntity(
    val chatId:Long,
    val massageId:Int,
    @ManyToOne var user: User,
    @ManyToOne var group: Group,
    @Enumerated(EnumType.STRING) val messageType:MessageType,
    @OneToOne val attachment: Attachment?,
    val text: String? = null,
    var caption: String? = null,
    var isActive: Boolean? = true
) : BaseEntity()


@Entity(name = "groups")
class Group(
    @ManyToOne var user: User? = null,
    @ManyToOne var operator: User? = null,
    @Enumerated(EnumType.STRING) var language: Language?,
    var isActive: Boolean = true,
    var ball:Int=0,
) : BaseEntity()

@Entity
class Operator(
    var name: String,
    var phoneNumber: String,
) : BaseEntity()

@Entity
class Attachment(
    var fileOriginalName:String,
    var size:Long,
    var contentType:String,
    var name:String
) :BaseEntity()
