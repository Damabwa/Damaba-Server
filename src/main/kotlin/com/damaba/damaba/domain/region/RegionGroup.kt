package com.damaba.damaba.domain.region

/**
 * 상위 카테고리(시/도)에 대한 행정 구역 목록을 다루는 domain entity.
 *
 * @property category 시/도 단위 지역의 상위 카테고리 (ex. "서울", "경기")
 * @property regions 시/군/구 단위의 지역 리스트 (ex. "강남", "서대문", "은평")
 * @see Region
 */
data class RegionGroup(
    val category: String,
    val regions: List<String>,
)
