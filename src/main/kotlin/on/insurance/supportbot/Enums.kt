package on.insurance.supportbot.teligram



enum class  BotStep{
    START,
    LANGUAGE,
    CONTACT,
    CHAT,
    CLOSE,
    BACK,
    BEGIN,
}
enum class  Language{
    UZ, RU , ENG;
}
enum class  Role{
    USER,ADMIN , OPERATOR
}