package com.proschek.exception

class TodoNotFoundException(message: String) : RuntimeException(message)
class InvalidTodoDataException(message: String) : RuntimeException(message)
class TodoAlreadyExistsException(message: String) : RuntimeException(message)
class TodoMongoException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)