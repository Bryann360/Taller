package com.example.backend.controller

import com.example.backend.model.Metrics
import com.example.backend.service.MetricsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metrics")
class MetricsController(
    private val metricsService: MetricsService
) {
    
    /**
     * GET /metrics
     * Returns CloudWatch-style metrics for the service
     */
    @GetMapping
    fun getMetrics(): ResponseEntity<Metrics> {
        return ResponseEntity.ok(metricsService.getMetrics())
    }
}
