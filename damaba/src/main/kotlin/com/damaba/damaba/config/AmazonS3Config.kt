package com.damaba.damaba.config

import com.damaba.user.property.AwsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AmazonS3Config(private val awsProperties: AwsProperties) {
    @Bean
    fun amazonS3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(awsProperties.s3.accessKey, awsProperties.s3.secretKey)
        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}
