package com.example.backend.service

import org.springframework.stereotype.Service

@Service
class HealthService {
    fun status(): Map<String, String> = mapOf("status" to "healthy")
}

