package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
@AutoConfigureMockMvc
internal class MarsPhotosControllerRoverProcessingIntegrationTest(
    @Autowired private val marsRovers: MarsRovers,
    @Autowired private val mockMvc: MockMvc,
) {
    @MockkBean(relaxed = true)
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `get photos for a date where no rovers were on mars does not call the nasa api`() {
        // the first rover in the list should be 'sprint'
        val rover = marsRovers.rovers[0]
        expectThat(rover.name) isEqualTo "sprint"

        // specify an earth date that is 20 days before the rover lands on Mars
        val earthDate = rover.landingDate.minusDays(20)

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isOk)
            .andDo(print())

        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/sprint?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/opportunity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/curiosity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `get photos only calls the nasa api for the rovers that are active on mars for the specified date`() {
        // only opportunity and curiosity should have been active on this date.  It was after sprint completed
        // its mission and before perseverance landed
        val earthDate = "2018-06-01"

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isOk)
            .andDo(print())

        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/sprint?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/opportunity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/curiosity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

}