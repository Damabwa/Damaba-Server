package com.damaba.damaba.util

import org.apache.commons.lang3.RandomStringUtils
import java.time.LocalDate
import java.time.LocalTime
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

        fun randomString(): String = randomString(10)

        fun randomString(len: Int): String = RandomStringUtils.random(len, true, true)

        fun randomLocalDate(): LocalDate = LocalDate.of(
            randomInt(positive = true) % 3000 + 1,
            randomInt(positive = true) % 12 + 1,
            randomInt(positive = true) % 25 + 1,
        )

        fun randomLocalTime(): LocalTime = LocalTime.of(
            randomInt(positive = true) % 24,
            randomInt(positive = true) % 60,
        )

        fun randomUrl(): String = "https://${randomString()}"

        fun <T> generateRandomList(maxSize: Int, generator: () -> T): List<T> =
            generateSequence { generator() }
                .take(randomInt(positive = true, min = 1, max = maxSize))
                .toList()

        fun <T> generateRandomSet(maxSize: Int, generator: () -> T): Set<T> =
            generateSequence { generator() }
                .take(randomInt(positive = true, min = 1, max = maxSize))
                .toSet()
    }
}
