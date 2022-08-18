package on.insurance.supportbot


import on.insurance.supportbot.teligram.Operator


open class OperatorCreateDto(
    var name: String,
    var phoneNumber: String

) {
    fun toEntity(): Operator = Operator(name, phoneNumber)
}

open class OperatorUpdateDto(
    var name: String? = null,
    var phoneNumber: String? = null
)

open class OperatorDto(
    var id:Long,
    var name: String,
    var phoneNumber:String

) {
    companion object {
        fun toDto(entity: Operator) = entity.run { OperatorDto(id!!, name, phoneNumber) }
    }
}


data class BaseMessage(val code: Int, val message: String)
