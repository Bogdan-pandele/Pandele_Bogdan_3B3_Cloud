package com.school.school_agregator.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

            registerModule(JavaTimeModule())

            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}