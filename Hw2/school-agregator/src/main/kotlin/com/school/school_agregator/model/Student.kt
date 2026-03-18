package com.school.school_agregator.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID
import kotlin.uuid.Uuid

data class Student(
    val id: UUID,
    val name: String,
    @JsonProperty("date_of_birth")
    val dateOfBirth: LocalDate,
    val gender: Gender,
    @JsonProperty("enrolement_date")
    val enrollmentDate: LocalDate
)