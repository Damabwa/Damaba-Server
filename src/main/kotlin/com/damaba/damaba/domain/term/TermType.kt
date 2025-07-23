package com.damaba.damaba.domain.term

enum class TermType(
    val required: Boolean,
    val detailUrl: String,
) {
    AGE_CONFIRMATION(
        required = true,
        detailUrl = "",
    ),
    SERVICE_TERMS(
        required = true,
        detailUrl = "https://obtainable-chocolate-d07.notion.site/1b53cdfe88408032b850cbba8f856532?source=copy_link",
    ),
    PRIVACY_TERMS(
        required = true,
        detailUrl = "https://www.notion.so/1b53cdfe884080ef94bcf7e9f55f0bcb",
    ),
    PHOTOGRAPHER_TERMS(
        required = true,
        detailUrl = "https://obtainable-chocolate-d07.notion.site/MVP-2343cdfe88408001ae94ce87fcc81ebb?source=copy_link",
    ),
}
