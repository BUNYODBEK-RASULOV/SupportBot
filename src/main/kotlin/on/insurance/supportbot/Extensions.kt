package on.insurance.supportbot

//fun Student.getFullName() = "$firstName $lastName $sureName"

fun Boolean.ifTrue(run: () -> Unit) {
    if (this) run()
}

fun Boolean.ifFalse(run: () -> Unit) {
    if (!this) run()
}


