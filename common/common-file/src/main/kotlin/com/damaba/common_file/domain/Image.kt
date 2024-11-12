package com.damaba.common_file.domain

class Image(name: String, url: String) : File(name, url) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false
        return super.equals(other)
    }

    override fun hashCode(): Int = super.hashCode()
}
