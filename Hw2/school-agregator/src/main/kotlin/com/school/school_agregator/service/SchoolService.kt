package com.school.school_agregator.service

import com.school.school_agregator.dto.GradeRequestDto
import com.school.school_agregator.dto.PatchClassRequestDto
import com.school.school_agregator.dto.PatchStudentRequestDto
import com.school.school_agregator.model.Student
import com.school.school_agregator.model.Teacher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.http.HttpHeaders
import java.util.UUID
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import kotlin.math.log

@Service
class SchoolService {
    private val baseUrl = "http://localhost:8080"
    private val restTemplate = RestTemplate()

    private val logger = LoggerFactory.getLogger(SchoolService::class.java)
    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun getAllStudents(): Array<Student> {
        return restTemplate.getForObject("$baseUrl/students", Array<Student>::class.java) ?: emptyArray()
    }

    fun findStudentById(id: UUID): Student? {
        return  try {
            restTemplate.getForObject("$baseUrl/students/$id", Student::class.java)
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Not found: ${e.responseBodyAsString}")
            null
        }
    }

    fun getAllTeachers(): Array<Teacher> {
        return restTemplate.getForObject("$baseUrl/teachers", Array<Teacher>::class.java) ?: emptyArray()
    }

    fun findTeacherById(id: UUID): Teacher? {
        return try {
            restTemplate.getForObject("$baseUrl/teachers/$id", Teacher::class.java)
        } catch(e: HttpClientErrorException.NotFound) {
            val errorMessage = e.responseBodyAsString
            logger.warn("Not found: $e")
            null
        }
    }

    fun createStudent(s: Student) {
        try {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val jsonBody = objectMapper.writeValueAsString(s)

            val request = HttpEntity(jsonBody, headers)

            restTemplate.postForEntity("$baseUrl/students", request, String::class.java)

            logger.info("Student ${s.id} was successfully sent")
        } catch (e: Exception) {
            logger.error("Error at post: ${e.message}")
            throw e
        }
    }
    fun gradeStudent(studentId: UUID, score: Int, classId: UUID) {
        val payload = GradeRequestDto(classId, score)
        try {
            restTemplate.postForEntity("$baseUrl/students/$studentId/grades", payload, String::class.java)
            logger.info("Grade for student with id $studentId was successfully sent")
        } catch (e: HttpClientErrorException.Conflict) {
            logger.warn("Conflict: ${e.responseBodyAsString}")
            throw e
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Not found: ${e.responseBodyAsString}")
            throw e
        } catch (e: HttpClientErrorException.BadRequest) {
            logger.warn("Bad request: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun updateStudent(studentId: UUID, updatedStudent: Student) {
        try {
            restTemplate.put("$baseUrl/students/$studentId", updatedStudent, String::class.java)
            logger.info("Student with id $studentId has successfully been updated")
        } catch (e: HttpClientErrorException.BadRequest) {
            logger.warn("Bad request: ${e.responseBodyAsString}")
            throw e
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Not found: ${e.responseBodyAsString}")
            throw e
        }
    }
    fun updateStudentGrade(studentId: UUID, classId: UUID, newScore: Int) {
        val payload = mapOf("score" to newScore)
        try {
            restTemplate.put("$baseUrl/students/$studentId/grades/$classId", payload, String::class.java)
            logger.info("Score for student with id $studentId has successfully been updated  to $newScore for class $classId")
        } catch (e: HttpClientErrorException) {
            logger.warn("Error at updating score: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun deleteAllStudents() {
        try {
            restTemplate.delete("$baseUrl/students")
            logger.info("All students have been deleted")
        } catch (e: HttpClientErrorException.MethodNotAllowed) {
            logger.warn("Action rejected by server: ${e.responseBodyAsString}")
            throw e
        }

    }

    fun deleteStudent(studentId: UUID) {
        try {
            restTemplate.delete("$baseUrl/students/$studentId")
            logger.info("Student with id $studentId has successfully been deleted")
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Student with id $studentId not found")
            throw e
        }
    }

    fun deleteStudentGrade(studentId: UUID, classId: UUID) {
        try {
            restTemplate.delete("$baseUrl/students/$studentId/grades/$classId")
            logger.info("Grade for student with id $studentId and class $classId has successfully been deleted")
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Error at deleting: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun patchStudent(studentId: UUID, request: PatchStudentRequestDto) {
        try {
            restTemplate.patchForObject("$baseUrl/students/$studentId", request, String::class.java)
            logger.info("Student with id $studentId has been successfully patched")
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Not found at patching student: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun patchClass(classId: UUID, request: PatchClassRequestDto) {
        try {
            restTemplate.patchForObject("$baseUrl/classes/$classId", request, String::class.java)
            logger.info("Class with id $classId successfully patched")
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Not found at patching class: ${e.responseBodyAsString}")
            throw e
        }
    }
}
