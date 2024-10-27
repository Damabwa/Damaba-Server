package com.damaba.damaba.adapter.outbound.file

import com.damaba.common_file.application.port.outbound.DeleteFilePort
import com.damaba.common_file.application.port.outbound.UploadFilePort
import com.damaba.common_file.application.port.outbound.UploadFilesPort
import com.damaba.common_file.domain.UploadFile
import com.damaba.common_file.domain.UploadedFile
import com.damaba.damaba.property.AwsProperties
import com.damaba.user.property.DamabaProperties
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

    override fun upload(files: List<UploadFile>, path: String): List<UploadedFile> =
        files.map { file -> upload(file, path) }

    override fun delete(file: UploadedFile) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(awsProperties.s3.bucketName)
                .key(file.name)
                .build(),
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
