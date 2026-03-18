package com.school.school_agregator.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherResponseDto(
    val current: CurrentWeatherDto
)

data class CurrentWeatherDto(
    val temperature: Int,
    @JsonProperty("weather_descriptions")
    val weatherDescriptions: List<String>
)
