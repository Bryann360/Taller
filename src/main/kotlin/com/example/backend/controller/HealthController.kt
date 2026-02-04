package com.example.backend.controller

import com.example.backend.service.HealthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "Health check endpoints")
class HealthController(
    private val healthService: HealthService
) {

    @GetMapping("/health")
    @Operation(summary = "Service health", description = "Simple readiness/liveness health endpoint.")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(healthService.status())
    }
}
