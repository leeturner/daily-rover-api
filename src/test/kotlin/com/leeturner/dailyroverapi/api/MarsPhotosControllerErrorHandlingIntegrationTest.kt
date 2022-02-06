package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.verify
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
internal class MarsPhotosControllerErrorHandlingIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `get photos for a future date does not call the nasa api and returns a bad request`() {
        // specify an earth date that is in the future
        val earthDate = LocalDate.now().plusDays(20)

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("\$.errorCode", Matchers.`is`(400)))
            .andExpect(jsonPath("\$.message", Matchers.`is`("The earth date can not be a future date")))
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

}