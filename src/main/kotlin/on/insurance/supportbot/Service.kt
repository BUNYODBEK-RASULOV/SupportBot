package on.insurance.supportbot

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


interface OperatorService {
    fun create(dto: OperatorCreateDto)
    fun update(id: Long, dto: OperatorUpdateDto)
    fun get(id: Long): OperatorDto
    fun delete(id: Long)
    fun listOfOperator(): List<OperatorDto>
}

interface AuthService{
    fun login(dto:AuthDto):String
}



@Service
class OperatorServiceImpl(
    private val repository: OperatorRepository
) : OperatorService {
    override fun create(dto: OperatorCreateDto) {
        repository.save(dto.toEntity())
    }

    override fun update(id: Long, dto: OperatorUpdateDto) {
        val entity = repository.findByIdNotDeleted(id) ?: throw NullPointerException("we have not this operator")
        dto.run {
            name?.run { entity.name = this }
            phoneNumber?.run { entity.phoneNumber = this }
            repository.save(entity)
        }
    }

    override fun get(id: Long): OperatorDto = repository.findByIdNotDeleted(id)?.run { OperatorDto.toDto(this) }
        ?: throw NullPointerException("Couldn't find by id")


    override fun delete(id: Long) {
        repository.trash(id)
    }

    override fun listOfOperator() = repository.getAllOperator().map(OperatorDto.Companion::toDto)
}





