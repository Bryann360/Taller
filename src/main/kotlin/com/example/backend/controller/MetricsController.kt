package com.example.backend.controller

import com.example.backend.model.Metrics
import com.example.backend.service.MetricsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metrics")
@Tag(name = "Metrics", description = "Service metrics similar to CloudWatch")
class MetricsController(
    private val metricsService: MetricsService
) {

    @GetMapping
    @Operation(summary = "Get service metrics", description = "Returns collected counters for uploads, reads and errors.")
    fun getMetrics(): ResponseEntity<Metrics> {
        return ResponseEntity.ok(metricsService.getMetrics())
    }
}
