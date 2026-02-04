package com.example.backend.controller

import com.example.backend.model.FileRequest
import com.example.backend.model.FileResponse
import com.example.backend.service.MetricsService
import com.example.backend.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
class FileController(
    private val storageService: StorageService,
    private val metricsService: MetricsService
) {
    
    private val logger = LoggerFactory.getLogger(FileController::class.java)
    
    /**
     * POST /files
     * Upload a file to local storage (simulates S3 upload)
     */
    @PostMapping
    fun uploadFile(@RequestBody request: FileRequest): ResponseEntity<FileResponse> {
        return try {
            logger.info("Uploading file: ${request.filename}")
            storageService.saveFile(request.filename, request.content)
            metricsService.incrementUploads()
            
            ResponseEntity.status(HttpStatus.CREATED).body(
                FileResponse(
                    filename = request.filename,
                    message = "File uploaded successfully"
                )
            )
        } catch (e: Exception) {
            logger.error("Error uploading file: ${request.filename}", e)
            metricsService.incrementErrors()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                FileResponse(
                    filename = request.filename,
                    message = "Error uploading file: ${e.message}"
                )
            )
        }
    }
    
    /**
     * GET /files/{filename}
     * Retrieve file content from local storage (simulates S3 download)
     */
    @GetMapping("/{filename}")
    fun getFile(@PathVariable filename: String): ResponseEntity<String> {
        logger.info("Reading file: $filename")
        
        val content = storageService.readFile(filename)
        
        return if (content != null) {
            metricsService.incrementReads()
            ResponseEntity.ok(content)
        } else {
            metricsService.incrementErrors()
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: $filename")
        }
    }
    
    /**
     * GET /files
     * List all files in storage (bonus endpoint)
     */
    @GetMapping
    fun listFiles(): ResponseEntity<List<String>> {
        val files = storageService.listFiles()
        return ResponseEntity.ok(files)
    }
}
