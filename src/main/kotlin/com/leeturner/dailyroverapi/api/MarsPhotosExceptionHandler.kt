package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.api.response.ErrorResponse
import com.leeturner.dailyroverapi.exception.FutureDateException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class MarsPhotosExceptionHandler {

  @ExceptionHandler(FutureDateException::class)
  fun handleFutureDateException(e: FutureDateException): ResponseEntity<ErrorResponse> {
    logger.error { "FutureDateException thrown due to earth date being in the future" }
    return ResponseEntity.badRequest()
        .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.message ?: ""))
  }
}
