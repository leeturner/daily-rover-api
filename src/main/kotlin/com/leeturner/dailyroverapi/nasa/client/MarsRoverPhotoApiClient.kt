package com.leeturner.dailyroverapi.nasa.client

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.photo.Photo
import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Component
class MarsRoverPhotoApiClient(
    val restTemplate: RestTemplate,
) {

    @Retryable(value = [ResourceAccessException::class], maxAttempts = 2)
    fun getRoverPhotosByDate(earthDate: LocalDate, rover: Rover): List<Photo> {
        logger.info { "Calling Mars rover Photo API for rover ${rover.name} for earthDate $earthDate" }
        val responseEntity =
            this.restTemplate.getForEntity("${rover.photoApiUrl}&earth_date=$earthDate", NasaPhotoResponse::class.java)
        val nasaPhotoResponse = when (responseEntity.statusCode) {
            // we have a successful response, so we should be able to get the results, if not then an empty result
            HttpStatus.OK -> responseEntity.body ?: NasaPhotoResponse(emptyList())
            // we get a 403 FORBIDDEN if the API key is incorrect
            HttpStatus.FORBIDDEN -> {
                logger.error { "403 FORBIDDEN response returned from the NASA API, check that your API key is correct. Status: ${responseEntity.statusCode}, body: ${responseEntity.body}" }
                NasaPhotoResponse(emptyList())
            }
            // any other status will just return an empty list of photos.
            else -> {
                logger.error { "Non OK response returned from the NASA API. Status: ${responseEntity.statusCode}, body: ${responseEntity.body}" }
                NasaPhotoResponse(emptyList())
            }
        }
        return nasaPhotoResponse.photos
    }

    @Recover
    fun apiAccessResourceAccessExceptionRecovery(resourceAccessException: ResourceAccessException): List<Photo> {
        logger.error(resourceAccessException) { "Cannot access Mars rover photo API - Reason: ${resourceAccessException.message}" }
        return emptyList()
    }

//     TODO: figure out how to deal with the rate limiting - X-RateLimit-Limit: 40 & X-RateLimit-Remaining: 35
//     TODO: deal with other response code:
//     invalid rover name = 400 bad request with the error response:
//    {"errors":"Invalid Rover Name"}
}