package com.leeturner.dailyroverapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class DailyRoverApiApplication

fun main(args: Array<String>) {
	runApplication<DailyRoverApiApplication>(*args)
}
