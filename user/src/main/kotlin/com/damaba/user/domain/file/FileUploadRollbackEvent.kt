package com.damaba.user.domain.file

data class FileUploadRollbackEvent(val uploadedFiles: List<UploadedFile>) {
    constructor(uploadedFile: UploadedFile) : this(listOf(uploadedFile))
}
