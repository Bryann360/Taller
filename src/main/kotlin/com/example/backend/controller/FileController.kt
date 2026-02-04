package com.example.backend.controller

import com.example.backend.model.FileRequest
import com.example.backend.model.FileResponse
import com.example.backend.service.FileService
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.validation.annotation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
@Tag(name = "Files", description = "Manage file upload, download and listing")
@Validated
class FileController(
    private val fileService: FileService
) {

    @PostMapping
    @Operation(
        summary = "Upload a file",
        description = "Uploads a file to local storage (simulates S3 upload)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "File uploaded"),
            ApiResponse(responseCode = "409", description = "File already exists",
                content = [Content(schema = Schema(implementation = com.example.backend.model.ApiError::class))]),
            ApiResponse(responseCode = "500", description = "Storage error",
                content = [Content(schema = Schema(implementation = com.example.backend.model.ApiError::class))])
        ]
    )
    fun uploadFile(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "File payload to upload",
            required = true,
            content = [Content(schema = Schema(implementation = FileRequest::class))]
        )
        @RequestBody @Valid request: FileRequest
    ): ResponseEntity<FileResponse> {
        val response = fileService.upload(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{filename}")
    @Operation(
        summary = "Get file contents",
        description = "Retrieves file content from local storage (simulates S3 download)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "File found"),
            ApiResponse(responseCode = "404", description = "File not found",
                content = [Content(schema = Schema(implementation = com.example.backend.model.ApiError::class))]),
            ApiResponse(responseCode = "500", description = "Storage error",
                content = [Content(schema = Schema(implementation = com.example.backend.model.ApiError::class))])
        ]
    )
    fun getFile(
        @Parameter(description = "Name of the file", required = true)
        @PathVariable @Size(min = 1, max = 255) filename: String
    ): ResponseEntity<String> {
        val content = fileService.read(filename)
        return ResponseEntity.ok(content)
    }

    @GetMapping
    @Operation(
        summary = "List files",
        description = "Lists all files in storage."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Files listed"),
            ApiResponse(responseCode = "500", description = "Storage error",
                content = [Content(schema = Schema(implementation = com.example.backend.model.ApiError::class))])
        ]
    )
    fun listFiles(): ResponseEntity<List<String>> {
        val files = fileService.list()
        return ResponseEntity.ok(files)
    }
}
