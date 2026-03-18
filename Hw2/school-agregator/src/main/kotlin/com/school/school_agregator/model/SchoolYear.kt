package com.school.school_agregator.model

import java.time.LocalDate
import java.util.UUID

data class SchoolYear(
    val id: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val year: Int
)