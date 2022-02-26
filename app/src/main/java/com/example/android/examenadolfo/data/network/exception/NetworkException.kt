package com.example.android.examenadolfo.data.network.exception

class NetworkException(
    var httpCode: Int,
    var errorCode: Int = -1,
    var exception: String?,
    override val message: String
) : RuntimeException(message)