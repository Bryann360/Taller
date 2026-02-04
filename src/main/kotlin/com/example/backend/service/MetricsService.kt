package com.example.backend.service

import com.example.backend.model.Metrics
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

/**
 * In-memory metrics service simulating CloudWatch metrics.
 * Uses atomic counters for thread-safe metric tracking.
 */
@Service
class MetricsService {
    
    private val uploadCount = AtomicLong(0)
    private val readCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    
    fun incrementUploads() {
        uploadCount.incrementAndGet()
    }
    
    fun incrementReads() {
        readCount.incrementAndGet()
    }
    
    fun incrementErrors() {
        errorCount.incrementAndGet()
    }
    
    fun getMetrics(): Metrics {
        return Metrics(
            uploads = uploadCount.get(),
            reads = readCount.get(),
            errors = errorCount.get()
        )
    }
    
    /**
     * Reset metrics - useful for testing
     */
    fun reset() {
        uploadCount.set(0)
        readCount.set(0)
        errorCount.set(0)
    }
}
