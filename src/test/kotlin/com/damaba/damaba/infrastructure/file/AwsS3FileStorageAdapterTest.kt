package com.damaba.damaba.infrastructure.file

import com.damaba.damaba.domain.file.File
import com.damaba.damaba.property.AwsProperties
import com.damaba.damaba.property.DamabaProperties
import com.damaba.damaba.util.RandomTestUtils.Companion.generateRandomList
import com.damaba.damaba.util.RandomTestUtils.Companion.randomString
import com.damaba.damaba.util.fixture.FileFixture.createUploadFile
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.util.stream.Stream
import kotlin.test.Test

class AwsS3FileStorageAdapterTest {
    private val s3Client: S3Client = mockk()
    private val damabaProperties: DamabaProperties = mockk()
    private val awsProperties: AwsProperties = mockk()
    private val sut: AwsS3FileStorageManager = AwsS3FileStorageManager(s3Client, damabaProperties, awsProperties)

    @BeforeEach
    fun setProperties() {
        every { awsProperties.s3.bucketName } returns "s3-bucket-name"
        every { damabaProperties.fileServerUrl } returns "https://file-server-url.test"
    }

    @MethodSource("uploadFileNames")
    @ParameterizedTest
    fun `업로드할 파일과 경로가 주어지고, 파일을 업로드하면, 업로드된 파일 정보가 반환된다`(uploadFileName: String?) {
        // given
        val uploadFile = createUploadFile(name = uploadFileName)
        val uploadPath = randomString()
        every {
            s3Client.putObject(any(PutObjectRequest::class), any(RequestBody::class))
        } returns PutObjectResponse.builder().build()

        // when
        val result = sut.upload(uploadFile, uploadPath)

        // then
        verify { s3Client.putObject(any(PutObjectRequest::class), any(RequestBody::class)) }
        confirmVerifiedEveryMocks()
        assertThat(result.name).isNotBlank()
        assertThat(result.url).isNotBlank()
    }

    @Test
    fun `업로드할 파일들과 경로가 주어지고, 파일들을 업로드하면, 업로드된 파일들의 정보가 반환된다`() {
        // given
        val uploadFiles = generateRandomList(maxSize = 5) { createUploadFile() }
        val uploadPath = randomString()
        every {
            s3Client.putObject(any(PutObjectRequest::class), any(RequestBody::class))
        } returns PutObjectResponse.builder().build()

        // when
        val result = sut.upload(uploadFiles, uploadPath)

        // then
        verify(exactly = uploadFiles.size) { s3Client.putObject(any(PutObjectRequest::class), any(RequestBody::class)) }
        confirmVerifiedEveryMocks()
        assertThat(result.size).isEqualTo(uploadFiles.size)
    }

    @Test
    fun `삭제할 파일 정보가 주어지고, 파일을 삭제한다`() {
        // given
        val fileForDelete = File(name = randomString(), url = randomString())
        every {
            s3Client.deleteObject(any(DeleteObjectRequest::class))
        } returns DeleteObjectResponse.builder().build()

        // when
        sut.delete(fileForDelete)

        // then
        verify { s3Client.deleteObject(any(DeleteObjectRequest::class)) }
        confirmVerifiedEveryMocks()
    }

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(s3Client)
    }

    companion object {
        @JvmStatic
        private fun uploadFileNames(): Stream<Arguments> = Stream.of(
            Arguments.of("test-image.jpg"),
            Arguments.of("test-image"),
            Arguments.of(""),
            Arguments.of(null),
        )
    }
}
