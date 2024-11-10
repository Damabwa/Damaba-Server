package com.damaba.common_file.domain

data class FileUploadRollbackEvent(val uploadedFiles: List<File>) {
    constructor(uploadedFile: File) : this(listOf(uploadedFile))
}
