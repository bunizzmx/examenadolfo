package  com.example.android.examenadolfo.data.network


import com.example.android.examenadolfo.data.network.exception.NetworkException
import com.example.android.examenadolfo.data.network.exception.ServiceException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

abstract class HandleServiceError {

    fun <T> handleResponse(response: Response<T>) {
        if (!response.isSuccessful) {
            if(response.code()== HttpURLConnection.HTTP_UNAVAILABLE){
                throw NetworkException(
                    httpCode = response.code(),
                    exception = response.message(),
                    message = "El servidor no puede atender temporalmente su solicitud debido a tiempo de inactividad o mantenimiento. Por favor, inténtelo de nuevo más tarde"
                )
            }
            try {
                if (response.errorBody() != null) {
                    val errorResponse = response.errorBody()!!.string()
                    val error = Gson().fromJson(errorResponse, ServiceError::class.java)
                    if (error.errorCode == ERROR_CODE_CABLE) {
                        throw ServiceException(
                            response.code(),
                            error.errorCode,
                            error.exception,
                            error.errorCode.toString()
                        )
                    } else {
                        throw ServiceException(
                            response.code(),
                            error.errorCode,
                            error.exception,
                            error.message
                        )
                    }
                } else {
                    throw ServiceException(response.code(), message = response.message())
                }
            } catch (exception: IOException) {
                throw NetworkException(
                    response.code(),
                    exception = exception.message,
                    message = response.message()
                )
            } catch (exception: JsonParseException) {
                throw NetworkException(
                    response.code(),
                    exception = exception.message,
                    message = response.message()
                )
            }
        }
    }

    open fun <T> applyRequestBody(input: T): RequestBody {
        val builder = GsonBuilder().disableHtmlEscaping()
            .create()

        return builder.toJson(input)
            .replace("\\n", "")
            .toRequestBody(APPLICATION_JSON)

    }

    companion object {
        val APPLICATION_JSON: MediaType = "application/json; charset=utf-8".toMediaType()

        const val ERROR_CODE_CABLE: Int = 1103
    }
}