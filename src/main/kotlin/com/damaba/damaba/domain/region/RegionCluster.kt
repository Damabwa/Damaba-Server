package com.damaba.damaba.domain.region

/**
 * 상위 카테고리(시/도)에 대한 군집화된 행정 구역 목록을 다루는 domain entity.
 *
 * @property category 군집화된 시/도 단위 지역의 상위 카테고리 (ex. "서울", "경기")
 * @property clusters 군집화된 시/군/구 단위의 지역 리스트 (ex. "강남", "서대문/은평", "중랑/노원/성북/동대문")
 */
data class RegionCluster(
    val category: String,
    val clusters: List<String>,
)
