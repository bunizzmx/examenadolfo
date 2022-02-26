package com.example.android.examenadolfo.data.network.exception

class ServiceException(
    val httpCode: Int,
    val errorCode: Int = -1,
    val exception: String? = "",
    override val message: String
) : RuntimeException(message)