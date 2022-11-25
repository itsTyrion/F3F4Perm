package de.redgames.f3nperm.reflection

class ReflectionException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}