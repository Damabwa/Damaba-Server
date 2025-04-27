package com.damaba.damaba.domain.file

import java.util.Objects

open class File(val name: String, val url: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is File) return false
        return this.name == other.name && this.url == other.url
    }

    override fun hashCode(): Int = Objects.hash(name, url)
}
