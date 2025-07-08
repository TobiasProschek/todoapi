package com.proschek.exception

class TodoNotFoundException(message: String) : RuntimeException(message)
class TodoInvalidDataException(message: String) : RuntimeException(message)
class TodoMongoException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)