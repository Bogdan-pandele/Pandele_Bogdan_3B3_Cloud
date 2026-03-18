package com.school.school_agregator.service

import com.school.school_agregator.dto.WeatherResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherService {
    private val restTemplate = RestTemplate()

    @Value("\${weatherstack.api-key}")
    private lateinit var apiKey: String

    @Value("\${weatherstack.base-url}")
    private lateinit var baseUrl: String

    @Cacheable("weather")
    fun getCurrentWeather(city: String): String {
        val url = "$baseUrl?access_key=$apiKey&query=$city"

        return try {
            val response = restTemplate.getForObject(url, WeatherResponseDto::class.java)

            if (response != null && response.current.weatherDescriptions.isNotEmpty()) {
                val temp = response.current.temperature
                val desc = response.current.weatherDescriptions[0]
                "It  is $temp degrees and $desc in $city"
            } else {
                "Weather data unavailable"
            }
        } catch (e: Exception) {
            println("ERROR: ${e.message}")
            "Could not fetch weather data"
        }
    }

    fun isGoodWeather(city: String): Boolean {
        val url = "$baseUrl?access_key=$apiKey&query=$city"
        return try {
            val response = restTemplate.getForObject(url, WeatherResponseDto::class.java)
            val temp = response?.current?.temperature ?: 0
            val description = response?.current?.weatherDescriptions?.firstOrNull()?.lowercase() ?: ""
            val isBadWeather = description.contains("rain") || description.contains("snow") || description.contains("storm")
            temp > 15 && !isBadWeather
        } catch (e: Exception) {
            false
        }
    }
}