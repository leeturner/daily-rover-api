package com.leeturner.dailyroverapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.leeturner.dailyroverapi.nasa.model.NasaResponse

object TestUtils {

    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    fun getNasaResponse(fileName: String): NasaResponse {
        val inputStream = this.javaClass.getResourceAsStream("/json/nasa-response/$fileName")
        return objectMapper.readValue(inputStream, NasaResponse::class.java)
    }
}