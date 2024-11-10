package com.damaba.common_file.domain

data class DeleteFileEvent(val urls: List<String>) {
    constructor(url: String) : this(listOf(url))
}
