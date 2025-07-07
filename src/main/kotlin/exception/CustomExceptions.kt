package com.proschek.exception

class TodoNotFoundException(message: String) : RuntimeException(message)
class TodoInvalidDataException(message: String) : RuntimeException(message)
class TodoAlreadyExistsException(message: String) : RuntimeException(message)
class TodoMongoException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
class TodoUUIDCreationException(message: String) : RuntimeException(message)