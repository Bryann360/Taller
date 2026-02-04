package com.example.backend.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import jakarta.annotation.PostConstruct

/**
 * Local storage service simulating Amazon S3 operations.
 * Files are stored in a local directory (/storage by default).
 */
@Service
class StorageService(
    @Value("\${storage.path:/storage}") private val storagePath: String
) {
    
    private val logger = LoggerFactory.getLogger(StorageService::class.java)
    private lateinit var storageDirectory: Path
    
    @PostConstruct
    fun init() {
        storageDirectory = Paths.get(storagePath)
        if (!Files.exists(storageDirectory)) {
            Files.createDirectories(storageDirectory)
            logger.info("Created storage directory: $storagePath")
        }
        logger.info("Storage service initialized at: $storagePath")
    }
    
    /**
     * Save file to local storage (simulates S3 PutObject)
     */
    fun saveFile(filename: String, content: String) {
        val sanitizedFilename = sanitizeFilename(filename)
        val filePath = storageDirectory.resolve(sanitizedFilename)
        Files.writeString(filePath, content)
        logger.info("File saved: $sanitizedFilename")
    }
    
    /**
     * Read file from local storage (simulates S3 GetObject)
     * Returns null if file doesn't exist
     */
    fun readFile(filename: String): String? {
        val sanitizedFilename = sanitizeFilename(filename)
        val filePath = storageDirectory.resolve(sanitizedFilename)
        
        return if (Files.exists(filePath)) {
            val content = Files.readString(filePath)
            logger.info("File read: $sanitizedFilename")
            content
        } else {
            logger.warn("File not found: $sanitizedFilename")
            null
        }
    }
    
    /**
     * Check if file exists (simulates S3 HeadObject)
     */
    fun fileExists(filename: String): Boolean {
        val sanitizedFilename = sanitizeFilename(filename)
        return Files.exists(storageDirectory.resolve(sanitizedFilename))
    }
    
    /**
     * List all files in storage (simulates S3 ListObjects)
     */
    fun listFiles(): List<String> {
        return Files.list(storageDirectory)
            .filter { Files.isRegularFile(it) }
            .map { it.fileName.toString() }
            .toList()
    }
    
    /**
     * Sanitize filename to prevent path traversal attacks
     */
    private fun sanitizeFilename(filename: String): String {
        return filename
            .replace("..", "")
            .replace("/", "")
            .replace("\\", "")
            .trim()
    }
}
