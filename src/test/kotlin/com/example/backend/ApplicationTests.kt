package com.example.backend

import com.example.backend.model.FileRequest
import com.example.backend.service.MetricsService
import com.example.backend.service.StorageService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.file.Path

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @Autowired
    private lateinit var metricsService: MetricsService
    
    @BeforeEach
    fun setup() {
        metricsService.reset()
    }
    
    @Test
    fun `health endpoint returns healthy status`() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("healthy"))
    }
    
    @Test
    fun `upload file returns created status`() {
        val request = FileRequest("test.txt", "Hello World")
        
        mockMvc.perform(
            post("/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.filename").value("test.txt"))
            .andExpect(jsonPath("$.message").value("File uploaded successfully"))
    }
    
    @Test
    fun `get non-existent file returns 404`() {
        mockMvc.perform(get("/files/nonexistent.txt"))
            .andExpect(status().isNotFound)
    }
    
    @Test
    fun `upload and retrieve file works correctly`() {
        val request = FileRequest("hello.txt", "Hello Nike")
        
        // Upload
        mockMvc.perform(
            post("/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
        
        // Retrieve
        mockMvc.perform(get("/files/hello.txt"))
            .andExpect(status().isOk)
            .andExpect(content().string("Hello Nike"))
    }
    
    @Test
    fun `metrics endpoint returns correct counts`() {
        // Initial state
        mockMvc.perform(get("/metrics"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.uploads").value(0))
            .andExpect(jsonPath("$.reads").value(0))
            .andExpect(jsonPath("$.errors").value(0))
        
        // Upload a file
        val request = FileRequest("metrics-test.txt", "Test content")
        mockMvc.perform(
            post("/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        
        // Read the file
        mockMvc.perform(get("/files/metrics-test.txt"))
        
        // Try to read non-existent file (error)
        mockMvc.perform(get("/files/does-not-exist.txt"))
        
        // Check metrics
        mockMvc.perform(get("/metrics"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.uploads").value(1))
            .andExpect(jsonPath("$.reads").value(1))
            .andExpect(jsonPath("$.errors").value(1))
    }
    
    @Test
    fun `list files endpoint works`() {
        mockMvc.perform(get("/files"))
            .andExpect(status().isOk)
    }
}
