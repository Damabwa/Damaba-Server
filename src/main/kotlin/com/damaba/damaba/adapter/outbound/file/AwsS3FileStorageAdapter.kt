package com.damaba.damaba.adapter.outbound.file

import com.damaba.damaba.application.port.outbound.file.DeleteFilePort
import com.damaba.damaba.application.port.outbound.file.UploadFilePort
import com.damaba.damaba.application.port.outbound.file.UploadFilesPort
import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.UploadFile
import com.damaba.damaba.property.AwsProperties
import com.damaba.damaba.property.DamabaProperties
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Component
class AwsS3FileStorageAdapter(
    private val s3Client: S3Client,
    private val damabaProperties: DamabaProperties,
    private val awsProperties: AwsProperties,
) : UploadFilePort,
    UploadFilesPort,
    DeleteFilePort {

    override fun upload(file: UploadFile, path: String): File {
        val originalFileName = if (file.name.isNullOrBlank()) UNKNOWN_FILE_NAME else file.name
        val storedFileName = generateStoredFileName(originalFileName, path)
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(awsProperties.s3.bucketName)
                .key(storedFileName)
                .contentType(file.contentType)
                .contentLength(file.size)
                .build(),
            RequestBody.fromInputStream(file.inputStream, file.size),
        )
        return File(
            name = originalFileName,
            url = "${damabaProperties.fileServerUrl}/$storedFileName",
        )
    }

    override fun upload(files: List<UploadFile>, path: String): List<File> = files.map { file -> upload(file, path) }

    override fun delete(file: File) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(awsProperties.s3.bucketName)
                .key(extractStoredFileName(file.url))
                .build(),
        )
    }

    private fun generateStoredFileName(fileName: String, uploadPath: String): String {
        val uuid = UUID.randomUUID().toString()

        if (fileName == UNKNOWN_FILE_NAME) {
            return "$uploadPath$uuid"
        }

        val extension = fileName.substringAfterLast(".", missingDelimiterValue = "")
        return if (extension.isBlank()) {
            "$uploadPath$uuid"
        } else {
            "$uploadPath$uuid.$extension"
        }
    }

    private fun extractStoredFileName(fileUrl: String): String = fileUrl.replace("${damabaProperties.fileServerUrl}/", "")

    companion object {
        private const val UNKNOWN_FILE_NAME = "unknown"
    }
}
