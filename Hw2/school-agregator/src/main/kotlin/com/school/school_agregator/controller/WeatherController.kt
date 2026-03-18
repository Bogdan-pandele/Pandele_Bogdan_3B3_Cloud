package com.school.school_agregator.controller

import com.school.school_agregator.service.WeatherService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/weather")
class WeatherController(private val weatherService: WeatherService) {

    @GetMapping("/current")
    fun getCurrentWeather(@RequestParam city: String): ResponseEntity<Map<String, String>> {
        val weatherInfo = weatherService.getCurrentWeather(city)
        return ResponseEntity.ok(mapOf("weather_info" to weatherInfo))
    }

    @GetMapping("/is-good")
    fun isGoodWeather(@RequestParam city: String): ResponseEntity<Map<String, Boolean>> {
        val isGood = weatherService.isGoodWeather(city)
        return ResponseEntity.ok(mapOf("is_good_weather" to isGood))
    }
}