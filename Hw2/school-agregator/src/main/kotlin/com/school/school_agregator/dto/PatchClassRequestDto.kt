package com.school.school_agregator.dto

import java.time.LocalDate
import java.util.UUID

data class PatchClassRequestDto(
    val teacherId: UUID? = null,
    val endPeriod: LocalDate? = null
)