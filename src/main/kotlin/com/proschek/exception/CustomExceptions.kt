package com.proschek.exception

/** Exception thrown when a requested todo item cannot be found */
class TodoNotFoundException(
    message: String,
) : RuntimeException(message)

/** Exception thrown when todo data is invalid or malformed */
class TodoInvalidDataException(
    message: String,
) : RuntimeException(message)

/** Exception thrown when a MongoDB operation fails during todo processing. */
class TodoMongoException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
