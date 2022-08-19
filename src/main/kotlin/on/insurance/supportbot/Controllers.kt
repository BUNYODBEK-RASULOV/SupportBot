package on.insurance.supportbot


import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/operator")
class OperatorController(private val service: OperatorService) {
    @PostMapping
    fun create(@RequestBody dto:  OperatorCreateDto) = service.create(dto)

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): OperatorDto = service.get(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: OperatorUpdateDto) = service.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("list")
    fun getAllList(): List<OperatorDto> = service.listOfOperator()



}
