package com.damaba.damaba.util

import org.apache.commons.lang3.RandomStringUtils
import java.time.LocalDate
import kotlin.random.Random

class RandomTestUtils {
    companion object {
        fun randomInt(
            positive: Boolean = false,
            min: Int = Int.MIN_VALUE,
            max: Int = Int.MAX_VALUE,
        ): Int {
            if (positive) return Random.nextInt(from = 1, until = max)
            return Random.nextInt(from = min, until = max)
        }

        fun randomLong(positive: Boolean = false): Long {
            if (positive) return Random.nextLong(from = 1, until = Long.MAX_VALUE)
            return Random.nextLong()
        }

        fun randomBoolean(): Boolean = Random.nextBoolean()

        fun randomString(): String = randomString(10)

        fun randomString(len: Int): String = RandomStringUtils.random(len, true, true)

        fun randomLocalDate(): LocalDate = LocalDate.of(
            randomInt(positive = true) % 3000 + 1,
            randomInt(positive = true) % 12 + 1,
            randomInt(positive = true) % 25 + 1,
        )

        fun <T> generateRandomList(maxSize: Int, generator: () -> T): List<T> =
            generateSequence { generator() }
                .take(randomInt(positive = true, max = maxSize))
                .toList()

        fun <T> generateRandomSet(maxSize: Int, generator: () -> T): Set<T> =
            generateSequence { generator() }
                .take(randomInt(positive = true, max = maxSize))
                .toSet()
    }
}
