package com.damaba.user.util

import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class RandomTestUtils {
    companion object {
        fun randomInt(
            positive: Boolean = false,
            min: Int = -2_000_000_000,
            max: Int = 2_000_000_000,
        ): Int {
            if (positive) return Random.nextInt(from = 1, until = max + 1)
            return Random.nextInt(from = min, until = max + 1)
        }

        fun randomLong(positive: Boolean = false): Long {
            if (positive) return Random.nextLong(from = 1, until = Long.MAX_VALUE)
            return Random.nextLong()
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
