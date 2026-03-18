package com.school.school_agregator.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.school.school_agregator.service.HolidayService
import com.school.school_agregator.service.SchoolService
import com.school.school_agregator.service.WeatherService

@RestController
@RequestMapping("/api/dashboard")
class DashboardController (
    private val schoolService: SchoolService,
    private val weatherService: WeatherService,
    private val holidayService: HolidayService
) {
    @GetMapping
    fun getDashboard(): ResponseEntity<Any> {
        val students = schoolService.getAllStudents()
        val weather = weatherService.getCurrentWeather("Bucharest")
        val holidays = holidayService.getNextHolidays().toList()

        val responseData: Map<String, Any?> = mapOf(
            "total_students" to students.size,
            "weather" to weather,
            "next_holidays" to  holidays.take(3).toList()
        )

        return ResponseEntity.ok(responseData)
    }
}