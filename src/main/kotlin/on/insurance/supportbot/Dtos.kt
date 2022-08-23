package on.insurance.supportbot


import on.insurance.supportbot.teligram.*
import javax.persistence.EnumType
import javax.persistence.Enumerated

interface GroupsByOperatorId {
    val kun: String
    val group: Group
}

data class GroupsByOperatorIdDto(
    var operator_id: Long,
    var first_day: String,
    var last_day: String,
) {

}


data class OperatorCreateDto(
    var name: String,
    var phoneNumber: String

) {
    fun toEntity(): Operator = Operator(name, phoneNumber)
}

data class OperatorUpdateDto(
    var name: String? = null,
    var phoneNumber: String? = null
)

data class OperatorDto(
    var id: Long,
    var name: String,
    var phoneNumber: String

) {
    companion object {
        fun toDto(entity: Operator) = OperatorDto(entity.id!!, entity.name, entity.phoneNumber)
    }
}


data class BaseMessage(val code: Int, val message: String)
data class UserDto(
    var id: Long,
    var chatId: Long,
    var botStep: BotStep,
    var language: Language,
    var isActive: Boolean

)

interface ResponseUser {
    var id: Long
    var telephoneNumber: String
    var fullName: String
    var systemLanguage: String
    var state:String

}
data class UserRequest(

    var fullName: String?=null,
    var state:BotStep?=null
)