package com.school.school_agregator.model

import java.util.UUID

data class StudentYear(
    val studentId: UUID,
    val  yearId: UUID,
    val averageScore: Int
)