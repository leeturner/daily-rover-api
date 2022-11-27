package com.leeturner.dailyroverapi.nasa.client

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

@SpringBootTest
internal class MarsRoverPhotoApiClientIntegrationTest(
    @Autowired private val marsRovers: MarsRovers,
    @Autowired private val marsRoverPhotoApiClient: MarsRoverPhotoApiClient
) {
    @MockkBean private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `get photos uses the correct url for the nasa rover photo api`() {
        val rover = marsRovers.rovers.last()
        expectThat(rover.name) isEqualTo "perseverance"

        // specify an earth date that is 20 days after the rover lands on mars
        val earthDate = rover.landingDate.plusDays(20)

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(NasaPhotoResponse(earthDate = earthDate, listOf()))
        }

        val photos = this.marsRoverPhotoApiClient.getRoverPhotosByDate(earthDate, rover)

        expectThat(photos).isEmpty()
        verify {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `when a ResourceAccessException is thrown the request is retried`() {
        val rover = marsRovers.rovers.last()
        expectThat(rover.name) isEqualTo "perseverance"

        // specify an earth date that is 20 days after the rover lands on mars
        val earthDate = rover.landingDate.plusDays(20)

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } throws ResourceAccessException("Cannot access API")

        val nasaPhotoResponse = this.marsRoverPhotoApiClient.getRoverPhotosByDate(earthDate, rover)

        verify(exactly = 2) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
        expectThat(nasaPhotoResponse).isEmpty()
    }

    @Test
    internal fun `when a 403 forbidden response is returned from the NASA API an empty list of photos is returned`() {
        val rover = marsRovers.rovers.last()
        expectThat(rover.name) isEqualTo "perseverance"

        // specify an earth date that is 20 days after the rover lands on mars
        val earthDate = rover.landingDate.plusDays(20)

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val photos = this.marsRoverPhotoApiClient.getRoverPhotosByDate(earthDate, rover)

        expectThat(photos).isEmpty()
        verify {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `when a non OK response is returned from the NASA API an empty list of photos is returned`() {
        val rover = marsRovers.rovers.last()
        expectThat(rover.name) isEqualTo "perseverance"

        // specify an earth date that is 20 days after the rover lands on mars
        val earthDate = rover.landingDate.plusDays(20)

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.status(HttpStatus.BAD_GATEWAY).build()
        }

        val photos = this.marsRoverPhotoApiClient.getRoverPhotosByDate(earthDate, rover)

        expectThat(photos).isEmpty()
        verify {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `when a ResourceAccessException is throw an empty list is returned`() {
        val photos = this.marsRoverPhotoApiClient.apiAccessResourceAccessExceptionRecovery(
            ResourceAccessException("API Access Error")
        )
        expectThat(photos).isEmpty()
    }
}