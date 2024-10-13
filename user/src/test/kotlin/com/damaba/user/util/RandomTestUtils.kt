package com.damaba.user.util

import java.util.UUID
import kotlin.random.Random

class RandomTestUtils {
    companion object {
        fun randomInt(): Int = Random.nextInt()
        fun randomLong(): Long = Random.nextLong()
        fun randomBoolean(): Boolean = Random.nextBoolean()
        fun randomString(): String = UUID.randomUUID().toString().substring(0, 16)
        fun randomString(len: Int): String = UUID.randomUUID().toString().substring(0, len)
    }
}
