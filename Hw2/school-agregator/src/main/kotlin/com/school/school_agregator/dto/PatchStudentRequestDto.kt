package com.school.school_agregator.dto

import com.school.school_agregator.model.Gender

data class PatchStudentRequestDto(
    val name: String? = null,
    val gender: Gender? = null
)