package com.school.school_agregator.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class GradeRequestDto(
    @JsonProperty("class_id")
    val classId: UUID,
    val score: Int
)
