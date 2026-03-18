package com.school.school_agregator.model

import java.util.UUID

data class StudentClass(
    val studentId: UUID,
    val classId: UUID,
    val score: Int
)