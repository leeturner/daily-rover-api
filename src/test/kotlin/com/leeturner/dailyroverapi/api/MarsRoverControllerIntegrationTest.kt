package com.leeturner.dailyroverapi.api

import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class MarsRoverControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {
  @Test
  internal fun `the rovers endpoint returns all rovers`() {
    this.mockMvc
        .perform(get("/v1/rovers/"))
        .andExpect(status().isOk)
        .andExpect(jsonPath("\$.length()", `is`(4)))
        .andDo(print())
  }
}
