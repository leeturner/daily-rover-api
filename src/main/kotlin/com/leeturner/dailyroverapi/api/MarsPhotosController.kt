package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.service.MarsPhotosService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@RestController("v1/photos")
class MarsPhotosController(
    val marsPhotosService: MarsPhotosService
) {

    @GetMapping("/{earthDate}")
    fun getPhotosByEarthDate(@PathVariable earthDate: LocalDate): ResponseEntity<NasaPhotoResponse> {
        // TODO: return a bad request if earthDate is in the future
        logger.info { "Earth date passed = $earthDate" }

        return ResponseEntity.ok(this.marsPhotosService.getMarsPhotosByEarthDate(earthDate))
    }
}