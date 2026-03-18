package com.school.school_agregator.config

import org.springframework.web.filter.CorsFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        config.allowedHeaders = listOf("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}