package on.insurance.supportbot.teligram

import on.insurance.supportbot.ifTrue

enum class  BotStep{
    START,
    LANGUAGE,
    CONTACT,
}
enum class  Language{
    UZ, RU , ENG;
}
enum class  Role{
    USER,ADMIN , OPERATOR
}