package com.damaba.user.util

import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class RandomTestUtils {
    companion object {
        fun randomInt(positive: Boolean = false, min: Int = 0, max: Int = Int.MAX_VALUE): Int {
            val positiveIntNumber = Random.nextInt(from = min, until = max)
            return if (positive) positiveIntNumber else -positiveIntNumber
        }

        fun randomLong(positive: Boolean = true, min: Long = 0, max: Long = Long.MAX_VALUE): Long {
            val positiveLongNumber = Random.nextLong(from = min, until = max)
            return if (positive) positiveLongNumber else -positiveLongNumber
        }

        fun randomBoolean(): Boolean = Random.nextBoolean()

        fun randomString(): String = UUID.randomUUID().toString().substring(0, 16)

        fun randomString(len: Int): String = UUID.randomUUID().toString().substring(0, len)

        fun randomLocalDate(): LocalDate = LocalDate.of(
            randomInt(positive = true) % 3000 + 1,
            randomInt(positive = true) % 12 + 1,
            randomInt(positive = true) % 25 + 1,
        )

        fun randomUrl(): String = "https://${randomString()}"
    }
}
