package com.bina.home.data.remote.exception

class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)