package com.proschek.utils

import java.util.UUID

/** Safely converts a string to UUID, returning null if the string is invalid. */
fun String?.toUUIDOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (ignored: IllegalArgumentException) {
        null
    }
