package on.insurance.supportbot.teligram



enum class  BotStep{
    START,
    LANGUAGE,
    CONTACT,
    CHAT,QUEUE,
    CLOSE, BEGIN, BACK

}
enum class  Language{
    UZ, RU , ENG;
}
enum class  Role{
    USER,ADMIN,OPERATOR

}
enum class ErrorCode(val code: Int) {
    GENERAL(100),
    OBJECT_NOT_FOUND(101)
}