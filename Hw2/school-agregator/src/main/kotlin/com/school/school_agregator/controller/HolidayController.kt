package com.school.school_agregator.controller


import com.school.school_agregator.dto.HolidayDto
import com.school.school_agregator.service.HolidayService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/api/holidays")
class HolidayController(private val holidayService: HolidayService) {
    @GetMapping
    fun getHolidays(@RequestParam year: Int, @RequestParam(defaultValue = "RO") countryCode: String): ResponseEntity<Array<HolidayDto>> {
        val holidays = holidayService.getHolidays(year, countryCode)
        return ResponseEntity.ok(holidays)
    }

    @GetMapping("/next")
    fun getNextHolidays(@RequestParam(defaultValue = "RO") countryCode: String): ResponseEntity<Array<HolidayDto>> {
        val holidays = holidayService.getNextHolidays(countryCode)
        return ResponseEntity.ok(holidays)
    }

    @GetMapping("/is-today")
    fun isTodayHoliday(@RequestParam(defaultValue = "RO") countryCode: String): ResponseEntity<Map<String, Boolean>> {
        val isHoliday = holidayService.isTodayHoliday(countryCode)
        return ResponseEntity.ok(mapOf("is_holiday" to isHoliday))
    }
}