package com.leeturner.dailyroverapi.api.response

data class ErrorResponse(
    val errorCode: Int = 200,
    val message: String = "",
)
