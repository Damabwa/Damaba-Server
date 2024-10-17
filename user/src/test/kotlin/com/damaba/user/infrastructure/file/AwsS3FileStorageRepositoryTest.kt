package com.damaba.user.infrastructure.file

import com.damaba.user.property.AwsProperties
import com.damaba.user.property.DamabaProperties
import com.damaba.user.util.RandomTestUtils.Companion.randomString
import com.damaba.user.util.TestFixture.createUploadFile
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
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.util.stream.Stream

class AwsS3FileStorageRepositoryTest {
    companion object {
        @JvmStatic
        private fun uploadFileNames(): Stream<Arguments> = Stream.of(
            Arguments.of("test-image.jpg"),
            Arguments.of("test-image"),
            Arguments.of(""),
            Arguments.of(null),
        )
    }

    private val s3Client: S3Client = mockk()
    private val damabaProperties: DamabaProperties = mockk()
    private val awsProperties: AwsProperties = mockk()
    private val sut: AwsS3FileStorageRepository = AwsS3FileStorageRepository(s3Client, damabaProperties, awsProperties)

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

    private fun confirmVerifiedEveryMocks() {
        confirmVerified(s3Client)
    }
}
