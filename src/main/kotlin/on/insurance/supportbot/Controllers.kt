package on.insurance.supportbot


import on.insurance.supportbot.teligram.Group
import on.insurance.supportbot.teligram.MessageEntity
import on.insurance.supportbot.teligram.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/operator")
class OperatorController(
    private val service: OperatorService,
    private val userService: UserService,
    private val groupService: GroupService,
    private val messageService: MessageService
) {
    @PostMapping
    fun create(@RequestBody dto: OperatorCreateDto) = service.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: OperatorUpdateDto) = service.update(id, dto)

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): OperatorDto = service.get(id)


    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping()
    fun getAllList(pageable: Pageable): Page<User> = userService.operatorList(pageable)

    @GetMapping("groupList")
    fun getAllGroupList(@RequestBody dto: GroupsByOperatorIdDto): List<GroupsByOperatorId> =
        groupService.groupsByOperatorId(dto)

    @GetMapping("messageList/{groupId}")
    fun getAllMessageList(@PathVariable groupId: Long): List<MessageEntity> =
        messageService.getAllMessageByGroupId(groupId)


}


@RestController
@RequestMapping("api/v1/user")
class UserController(private val userService: UserService) {

    @GetMapping()
    fun page(pageable: Pageable): Page<ResponseUser> = userService.userListWithPagination(pageable)

    @GetMapping("{id}")
    fun getUser(@PathVariable("id") id: Long): ResponseUser = userService.getContact(id)

    @PutMapping("{id}")
    fun editUser(@PathVariable("id") id: Long, @RequestBody userRequest: UserRequest) =
        userService.editUser(id, userRequest)
}

@RestController
@RequestMapping("api/v1/queue")
class QueueController(private val userService: UserService) {
    @GetMapping()
    fun queue(pageable: Pageable): Page<ResponseUser> = userService.queueListWithPagination(pageable)
}
