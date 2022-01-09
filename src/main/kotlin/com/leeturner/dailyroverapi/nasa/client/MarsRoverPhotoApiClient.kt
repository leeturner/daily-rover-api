package com.leeturner.dailyroverapi.nasa.client

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.photo.Photo
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import com.leeturner.dailyroverapi.nasa.model.rover.RoverStatus
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
    val marsRovers: MarsRovers
) {

    @Retryable(value = [ResourceAccessException::class], maxAttempts = 2)
    fun getPhotosByEarthDate(earthDate: LocalDate): NasaPhotoResponse {
        // TODO: use a map here to create the photoList ?
        val photoList: MutableList<Photo> = mutableListOf()
        this.marsRovers.rovers.forEach {
            photoList.addAll(this.getPhotosByEarthDateAndMarRover(earthDate = earthDate, rover = it))
        }
        return NasaPhotoResponse(photos = photoList)
    }

    fun getPhotosByEarthDateAndMarRover(earthDate: LocalDate, rover: Rover): List<Photo> {
        // first lets check to see if the specified rover was on Mars for the specified date
        if (earthDate < rover.landingDate) {
            logger.info { "Mars Rover (${rover.name}) was not on Mars for the specified date - $earthDate. No API call needs to be made" }
            return listOf()
        }

        // now lets check to see if the rover's mission had ended for the specified date
        rover.maxDate?.let {
            if (earthDate > rover.maxDate && rover.status == RoverStatus.COMPETE.status) {
                logger.info { "Mars Rover (${rover.name}) had ended its mission on Mars for the specified date - $earthDate. No API call needs to be made" }
                return listOf()
            }
        }

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
        // so we know we have a Mars rover that was on Mars for the given specified date
        return nasaPhotoResponse.photos
    }

    @Recover
    fun apiAccessResourceAccessExceptionRecovery(resourceAccessException: ResourceAccessException): NasaPhotoResponse {
        logger.error(resourceAccessException) {"Cannot access Mars rover photo API - Reason: ${resourceAccessException.message}"}
        return NasaPhotoResponse(emptyList())
    }

//     TODO: figure out how to deal with the rate limiting - X-RateLimit-Limit: 40 & X-RateLimit-Remaining: 35
//     TODO: deal with other response code:
//     success = 200 OK
//     wrong API Key = 403 Forbidden with the error response:
//    {
//        "error": {
//        "code": "API_KEY_INVALID",
//        "message": "An invalid api_key was supplied. Get one at https://api.nasa.gov:443"
//    }
//     invalid rover name = 400 bad request with the error response:
//    {"errors":"Invalid Rover Name"}
//     invalid data makes the api throw a 500 internal server error
}