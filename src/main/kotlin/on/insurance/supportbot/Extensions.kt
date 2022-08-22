package on.insurance.supportbot

fun User.createUser(chatId: Long): User {
    return User(chatId)
}

fun Boolean.ifTrue(run: () -> Unit) {
    if (this) run()
}

fun Boolean.ifFalse(run: () -> Unit) {
    if (!this) run()
}


// test