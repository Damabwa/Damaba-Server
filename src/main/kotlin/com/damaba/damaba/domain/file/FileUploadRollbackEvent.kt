package com.damaba.damaba.domain.file

data class FileUploadRollbackEvent(val uploadedFiles: List<File>) {
    constructor(uploadedFile: File) : this(listOf(uploadedFile))
}
