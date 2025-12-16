package com.bina.home.helper

import com.bina.home.data.remote.exception.NetworkException

object TestExceptionBuilder {

    const val NO_INTERNET_ERROR_MESSAGE = "No internet connection"
    const val API_ERROR_MESSAGE = "API error"
    const val REQUEST_TIMEOUT_ERROR_MESSAGE = "Request timeout"
    const val DATABASE_WRITE_ERROR_MESSAGE = "Failed to write to database"

    fun createNetworkException(
        message: String = NO_INTERNET_ERROR_MESSAGE
    ): NetworkException = NetworkException(message, null)

    fun createException(message: String): Exception = Exception(message)
    fun createRuntimeException(message: String): RuntimeException = RuntimeException(message)
}

