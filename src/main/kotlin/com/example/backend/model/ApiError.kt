package com.example.backend.model

import java.time.OffsetDateTime

data class ApiError(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?
)

