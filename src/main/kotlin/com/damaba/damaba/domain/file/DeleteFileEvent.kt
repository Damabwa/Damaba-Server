package com.damaba.damaba.domain.file

data class DeleteFileEvent(val urls: List<String>) {
    constructor(url: String) : this(listOf(url))
}
