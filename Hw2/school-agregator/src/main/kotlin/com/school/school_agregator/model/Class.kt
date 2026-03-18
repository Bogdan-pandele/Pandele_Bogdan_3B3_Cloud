package com.school.school_agregator.model

import java.time.LocalDate
import java.util.UUID

data class Class(
    val id: UUID,
    val subjectId: UUID,
    val teacherId: UUID,
    val startPeriod: LocalDate,
    val endPeriod: LocalDate,
    val schoolYearId: UUID,
)