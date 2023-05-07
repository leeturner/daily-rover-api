package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.exception.FutureDateException
import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.service.MarsPhotosService
import java.time.LocalDate
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("v1/photos")
class MarsPhotosController(val marsPhotosService: MarsPhotosService) {

  @GetMapping("/", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getPhotosForYesterday(): ResponseEntity<NasaPhotoResponse> {
    logger.info { "Getting photos for yesterday" }
    return this.getPhotosByEarthDate(LocalDate.now().minusDays(1))
  }

  @GetMapping("/{earthDate}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getPhotosByEarthDate(@PathVariable earthDate: LocalDate): ResponseEntity<NasaPhotoResponse> {
    // we can't return photos for future dates
    if (earthDate.isAfter(LocalDate.now())) {
      throw FutureDateException("The earth date can not be a future date")
    }
    logger.info { "Earth date passed = $earthDate" }
    return ResponseEntity.ok(this.marsPhotosService.getMarsPhotosByEarthDate(earthDate))
  }
}
