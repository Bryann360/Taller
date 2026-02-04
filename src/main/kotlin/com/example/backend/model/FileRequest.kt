package com.example.backend.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FileRequest(
    @field:NotBlank(message = "filename must not be blank")
    @field:Size(max = 255, message = "filename must be at most 255 characters")
    val filename: String,

    @field:NotBlank(message = "content must not be blank")
    val content: String
)
