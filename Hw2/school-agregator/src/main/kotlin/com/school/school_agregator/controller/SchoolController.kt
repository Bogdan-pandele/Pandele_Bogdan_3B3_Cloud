package com.school.school_agregator.controller

import com.school.school_agregator.dto.GradeRequestDto
import com.school.school_agregator.dto.PatchClassRequestDto
import com.school.school_agregator.dto.PatchStudentRequestDto
import com.school.school_agregator.model.Student
import com.school.school_agregator.model.Teacher
import com.school.school_agregator.service.SchoolService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID

@RestController
@RequestMapping("/api")
class SchoolController(private val schoolService: SchoolService) {

    @GetMapping("/students")
    fun getStudents(): ResponseEntity<Array<Student>> {
        return ResponseEntity.ok(schoolService.getAllStudents())
    }

    @GetMapping("/students/{id}")
    fun getStudent(@PathVariable id: UUID): ResponseEntity<Any> {
        val student = schoolService.findStudentById(id)

        return if(student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Student with $id not found"))
        }
    }

    @GetMapping("/teachers")
    fun getTeachers(): ResponseEntity<Array<Teacher>> {
        return ResponseEntity.ok(schoolService.getAllTeachers())
    }

    @GetMapping("/teachers/{id}")
    fun getTeacher(@PathVariable id: UUID): ResponseEntity<Any> {
        val teacher = schoolService.findTeacherById(id)

        return if(teacher != null) {
            ResponseEntity.ok(teacher)
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Teacher with $id not found"))
        }
    }

    @PostMapping("/students")
    fun createStudent(@RequestBody student: Student): ResponseEntity<Any> {
        return try {
            schoolService.createStudent(student)
            ResponseEntity.status(201).body(mapOf("message" to "Student created successfully"))
        } catch (e: HttpClientErrorException.Conflict) {
            ResponseEntity.status(409).body(mapOf("error" to "Student with this id already exists"))
        } catch (e: HttpClientErrorException.BadRequest) {
            ResponseEntity.status(400).body(mapOf("error" to "Invalid student data provided"))
        }
    }

    @PostMapping("/students/{id}/grades")
    fun gradeStudent(@PathVariable id: UUID, @RequestBody gradeRequestDto: GradeRequestDto): ResponseEntity<Any> {
        return try {
            schoolService.gradeStudent(id, gradeRequestDto.score, gradeRequestDto.classId)
            ResponseEntity.status(201).body(mapOf("message" to "Student graded successfully"))
        } catch (e: HttpClientErrorException.Conflict) {
            ResponseEntity.status(409).body(mapOf("error" to "Student is already graded for this class"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student with this id not  found"))
        } catch (e: HttpClientErrorException.BadRequest) {
            ResponseEntity.status(400).body(mapOf("error" to "Invalid score provided"))
        }
    }

    @PutMapping("/students/{id}")
    fun updateStudent(@PathVariable id: UUID, @RequestBody updatedStudent: Student): ResponseEntity<Any> {
        return try {
            schoolService.updateStudent(id, updatedStudent)
            ResponseEntity.ok(mapOf("message" to "Student updated successfully"))
        } catch (e: HttpClientErrorException.BadRequest) {
            ResponseEntity.status(400).body(mapOf("error" to "Invalid student data"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student with this id not found"))
        }
    }

    @PutMapping("/students/{id}/grades/{classId}")
    fun updateStudentGrade(@PathVariable id: UUID, @PathVariable classId: UUID, @RequestBody payload: Map<String, Int>): ResponseEntity<Any> {
        val newScore = payload["score"] ?: return ResponseEntity.status(400).body(mapOf("error" to "Invalid score value"))
        return try {
            schoolService.updateStudentGrade(id, classId, newScore)
            ResponseEntity.ok(mapOf("message" to "Score successfully updated"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student or class not found"))
        } catch (e: HttpClientErrorException.BadRequest) {
            ResponseEntity.status(400).body(mapOf("error" to "Invalid score value"))
        }
    }

    @DeleteMapping("/students")
    fun deleteAllStudents(): ResponseEntity<Any> {
        return try {
            schoolService.deleteAllStudents()
            ResponseEntity.ok(mapOf("message" to "All students have been deleted"))
        } catch (e: HttpClientErrorException.MethodNotAllowed) {
            ResponseEntity.status(405).body(mapOf("error" to "Not allowed to delete all students"))
        }
    }

    @DeleteMapping("/students/{id}/grades/{classId}")
    fun deleteStudentGrade(@PathVariable id: UUID, @PathVariable classId: UUID): ResponseEntity<Any> {
        return try {
            schoolService.deleteStudentGrade(id, classId)
            ResponseEntity.ok(mapOf("message" to "Successfully deleted grade"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student or class with id not found"))
        }
    }

    @DeleteMapping("/students/{id}")
    fun deleteStudent(@PathVariable id: UUID): ResponseEntity<Any> {
        return try {
            schoolService.deleteStudent(id)
            ResponseEntity.ok(mapOf("message" to "Successfully deleted student"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student with id not found"))
        }
    }

    @PatchMapping("/classes/{classId}")
    fun patchClass(@PathVariable classId: UUID, @RequestBody request: PatchClassRequestDto): ResponseEntity<Any> {
        return try {
            schoolService.patchClass(classId, request)
            ResponseEntity.ok(mapOf("message" to "Class successfully patched"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Class with id not found"))
        }
    }

    @PatchMapping("/students/{studentId}")
    fun patchStudent(@PathVariable studentId: UUID, @RequestBody request: PatchStudentRequestDto): ResponseEntity<Any> {
        return try {
            schoolService.patchStudent(studentId, request)
            ResponseEntity.ok(mapOf("message" to "Student successfully patched"))
        } catch (e: HttpClientErrorException.NotFound) {
            ResponseEntity.status(404).body(mapOf("error" to "Student with id not found"))
        }
    }
}