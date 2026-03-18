package com.school.school_agregator.service

import com.school.school_agregator.dto.HolidayDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class HolidayService {
    private val restTemplate = RestTemplate()
    private val baseUrl = "https://date.nager.at/api/v3"
    private val logger = LoggerFactory.getLogger(HolidayService::class.java)

    fun getHolidays(year: Int, countryCode: String = "RO"): Array<HolidayDto> {
        val url = "$baseUrl/PublicHolidays/$year/$countryCode"

        return try {
            restTemplate.getForObject(url, Array<HolidayDto>::class.java) ?: emptyArray()
        } catch (e: Exception) {
            logger.error("Error at calling Nager API: ${e.message}")
            emptyArray()
        }
    }

    fun getNextHolidays(countryCode: String = "RO"): Array<HolidayDto> {
        val url = "$baseUrl/NextPublicHolidays/$countryCode"

        return try {
            restTemplate.getForObject(url, Array<HolidayDto>::class.java) ?: emptyArray()
        } catch (e: Exception) {
            logger.error("Error at calling Nager API: ${e.message}")
            emptyArray()
        }
    }


    fun isTodayHoliday(countryCode: String = "RO"): Boolean {
        val url = "$baseUrl/IsTodayHoliday/$countryCode"
        return try {
            val response = restTemplate.getForEntity(url, String::class.java)
            response.statusCode.is2xxSuccessful
        } catch (e: Exception) {
            false
        }
    }

}