package com.example.backend.service

import com.example.backend.exception.NotFoundException
import com.example.backend.exception.StorageException
import com.example.backend.model.FileRequest
import com.example.backend.model.FileResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FileService(
    private val storageService: StorageService,
    private val metricsService: MetricsService
) {

    private val logger = LoggerFactory.getLogger(FileService::class.java)

    fun upload(request: FileRequest): FileResponse {
        try {
            logger.info("Uploading file: {}", request.filename)
            storageService.saveFile(request.filename, request.content)
            metricsService.incrementUploads()
            return FileResponse(
                filename = request.filename,
                message = "File uploaded successfully"
            )
        } catch (e: Exception) {
            logger.error("Error uploading file: {}", request.filename, e)
            metricsService.incrementErrors()
            throw StorageException("Error uploading file: ${e.message}", e)
        }
    }

    fun read(filename: String): String {
        logger.info("Reading file: {}", filename)
        val content = try {
            storageService.readFile(filename)
        } catch (e: Exception) {
            logger.error("Error reading file: {}", filename, e)
            metricsService.incrementErrors()
            throw StorageException("Error reading file: ${e.message}", e)
        }

        return if (content != null) {
            metricsService.incrementReads()
            content
        } else {
            metricsService.incrementErrors()
            throw NotFoundException("File not found: $filename")
        }
    }

    fun list(): List<String> {
        return try {
            storageService.listFiles()
        } catch (e: Exception) {
            logger.error("Error listing files", e)
            metricsService.incrementErrors()
            throw StorageException("Error listing files: ${e.message}", e)
        }
    }
}

