package com.school.school_agregator.model

import java.util.UUID

data class Subject(
    val id: UUID,
    val departmentId: UUID,
    val subjectName: String
)