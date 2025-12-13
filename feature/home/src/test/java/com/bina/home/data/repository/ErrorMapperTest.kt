package com.bina.home.data.repository

import com.bina.home.utils.UserError
import io.mockk.mockk
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.test.assertEquals

class ErrorMapperTest {

    private val errorMapper = ErrorMapper()

    @Test
    fun `mapErrorToUserError returns Network for SocketTimeoutException`() {
        val exception = SocketTimeoutException()

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Network, result)
    }

    @Test
    fun `mapErrorToUserError returns Network for ConnectException`() {
        val exception = ConnectException()

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Network, result)
    }

    @Test
    fun `mapErrorToUserError returns Network for IOException`() {
        val exception = IOException()

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Network, result)
    }

    @Test
    fun `mapErrorToUserError returns Unauthorized for HttpException 401`() {
        val exception = HttpException(Response.error<Any>(401, mockk(relaxed = true)))

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Unauthorized, result)
    }

    @Test
    fun `mapErrorToUserError returns NotFound for HttpException 404`() {
        val exception = HttpException(Response.error<Any>(404, mockk(relaxed = true)))

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.NotFound, result)
    }

    @Test
    fun `mapErrorToUserError returns Network for HttpException 500`() {
        val exception = HttpException(Response.error<Any>(500, mockk(relaxed = true)))

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Network, result)
    }

    @Test
    fun `mapErrorToUserError returns Unknown for unknown exception`() {
        val exception = RuntimeException()

        val result = errorMapper.mapErrorToUserError(exception)

        assertEquals(UserError.Unknown, result)
    }

    @Test
    fun `getMessage returns correct message for Network error`() {
        val message = UserError.Network.getMessage()

        assertEquals("Falha na conexão. Verifique sua internet.", message)
    }

    @Test
    fun `getMessage returns correct message for Unauthorized error`() {
        val message = UserError.Unauthorized.getMessage()

        assertEquals("Acesso não autorizado. Faça login novamente.", message)
    }

    @Test
    fun `getMessage returns correct message for NotFound error`() {
        val message = UserError.NotFound.getMessage()

        assertEquals("Recurso não encontrado no servidor.", message)
    }

    @Test
    fun `getMessage returns correct message for Unknown error`() {
        val message = UserError.Unknown.getMessage()

        assertEquals("Erro desconhecido. Tente novamente.", message)
    }

    @Test
    fun `getMessage returns custom message for Custom error`() {
        val customError = UserError.Custom("Erro customizado")

        val message = customError.getMessage()

        assertEquals("Erro customizado", message)
    }
}

