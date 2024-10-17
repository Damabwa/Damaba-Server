package com.damaba.user.infrastructure.file

import com.damaba.user.domain.file.FileStorageRepository
import com.damaba.user.domain.file.UploadFile
import com.damaba.user.domain.file.UploadedFile
import com.damaba.user.property.AwsProperties
import com.damaba.user.property.DamabaProperties
import org.springframework.stereotype.Repository
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Repository
class AwsS3FileStorageRepository(
    private val s3Client: S3Client,
    private val damabaProperties: DamabaProperties,
    private val awsProperties: AwsProperties,
) : FileStorageRepository {
    override fun upload(file: UploadFile, path: String): UploadedFile {
        val originalFileName = file.name
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
        return UploadedFile(
            name = storedFileName,
            url = "${damabaProperties.fileServerUrl}/$storedFileName",
        )
    }

    private fun generateStoredFileName(fileName: String?, uploadPath: String): String {
        val uuid = UUID.randomUUID().toString()
        if (fileName.isNullOrBlank()) {
            return "$uploadPath$uuid"
        }

        val extension = fileName.substringAfterLast(".", missingDelimiterValue = "")
        return if (extension.isBlank()) {
            "$uploadPath$uuid"
        } else {
            "$uploadPath$uuid.$extension"
        }
    }
}
