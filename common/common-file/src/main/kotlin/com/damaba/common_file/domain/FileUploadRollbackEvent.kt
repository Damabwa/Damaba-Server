package com.damaba.common_file.domain

data class FileUploadRollbackEvent(val uploadedFiles: List<UploadedFile>) {
    constructor(uploadedFile: UploadedFile) : this(listOf(uploadedFile))
}
