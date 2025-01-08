package com.damaba.damaba.util

import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class RandomTestUtils {
    companion object {
        fun randomInt(positive: Boolean = true, min: Int = 0, max: Int = Int.MAX_VALUE): Int {
            require(min < max) { "min should be less than max" }
            val value = Random.nextInt(min, max)
            return if (positive) value.coerceAtLeast(0) else value
        }

        fun randomLong(positive: Boolean = true, min: Long = 0, max: Long = Long.MAX_VALUE): Long {
            require(min < max) { "min should be less than max" }
            val value = Random.nextLong(min, max)
            return if (positive) value.coerceAtLeast(0) else value
        }

        fun randomBoolean(): Boolean = Random.nextBoolean()

        fun randomString(len: Int = 10): String = (1..len)
            .map { ('a'..'z') + ('A'..'Z') + ('0'..'9') }
            .flatten()
            .let { chars -> (1..len).map { chars.random(Random) } }
            .joinToString("")

        fun randomLocalDate(): LocalDate {
            val year = randomInt(positive = true, min = 1, max = 3000)
            val month = randomInt(positive = true, min = 1, max = 12)
            val day = randomInt(positive = true, min = 1, max = LocalDate.of(year, month, 1).lengthOfMonth())
            return LocalDate.of(year, month, day)
        }

        fun randomLocalTime(): LocalTime {
            val hour = randomInt(positive = true, min = 0, max = 24)
            val minute = randomInt(positive = true, min = 0, max = 60)
            return LocalTime.of(hour, minute)
        }

        fun randomUrl(): String = "https://${randomString()}"

        fun <T> generateRandomList(maxSize: Int, generator: () -> T): List<T> = generateSequence { generator() }
            .take(randomInt(min = 1, max = maxSize))
            .toList()

        fun <T> generateRandomSet(maxSize: Int, generator: () -> T): Set<T> = generateSequence { generator() }
            .take(randomInt(min = 1, max = maxSize))
            .toSet()
    }
}
