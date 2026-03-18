package com.school.school_agregator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SchoolAgregatorApplication

fun main(args: Array<String>) {
	runApplication<SchoolAgregatorApplication>(*args)
}
