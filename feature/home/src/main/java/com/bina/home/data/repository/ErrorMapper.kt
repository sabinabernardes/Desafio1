package com.bina.home.data.repository

import com.bina.home.utils.UserError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.ConnectException

internal class ErrorMapper {
    fun mapErrorToUserError(exception: Exception): UserError {
        return when (exception) {
            is SocketTimeoutException -> UserError.Network
            is ConnectException -> UserError.Network
            is IOException -> UserError.Network
            is HttpException -> when (exception.code()) {
                401 -> UserError.Unauthorized
                404 -> UserError.NotFound
                else -> UserError.Network
            }
            else -> UserError.Unknown
        }
    }
}

internal fun UserError.getMessage(): String = when (this) {
    UserError.Network -> "Falha na conexão. Verifique sua internet."
    UserError.NotFound -> "Recurso não encontrado no servidor."
    UserError.Unauthorized -> "Acesso não autorizado. Faça login novamente."
    UserError.Unknown -> "Erro desconhecido. Tente novamente."
    is UserError.Custom -> this.message
}

