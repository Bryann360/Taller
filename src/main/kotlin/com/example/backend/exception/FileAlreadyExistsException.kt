package com.example.backend.exception

class FileAlreadyExistsException(message: String? = null, cause: Throwable? = null) :
    ApiException(ErrorCode.RESOURCE_CONFLICT, message, cause)

