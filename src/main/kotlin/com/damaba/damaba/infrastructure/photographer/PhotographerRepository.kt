package com.damaba.damaba.infrastructure.photographer

import com.damaba.damaba.domain.common.Pagination
import com.damaba.damaba.domain.common.constant.PhotographyType
import com.damaba.damaba.domain.photographer.Photographer
import com.damaba.damaba.domain.photographer.PhotographerListItem
import com.damaba.damaba.domain.photographer.constant.PhotographerSortType
import com.damaba.damaba.domain.photographer.exception.PhotographerNotFoundException
import com.damaba.damaba.domain.region.RegionFilterCondition
import com.damaba.damaba.domain.user.exception.UserNotFoundException

interface PhotographerRepository {
    /**
     * `Photographer`를 저장한다.
     * `Photographer`는 `User`가 기존에 존재할 때만 생성/저장이 가능하다.
     *
     * @param photographer 저장할 `Photographer`
     * @return 저장된 `Photographer`
     * @throws UserNotFoundException 기존 유저 데이터가 존재하지 않는 경우.
     */
    fun createIfUserExists(photographer: Photographer): Photographer

    fun findPhotographerList(
        requestUserId: Long?,
        regions: Set<RegionFilterCondition>,
        photographyTypes: Set<PhotographyType>,
        searchKeyword: String?,
        sort: PhotographerSortType,
        page: Int,
        pageSize: Int,
    ): Pagination<PhotographerListItem>

    fun findSavedPhotographerList(
        requestUserId: Long?,
        page: Int,
        pageSize: Int,
    ): Pagination<PhotographerListItem>

    /**
     * 사진작가를 단건 조회한다.
     *
     * @param id 조회할 사진작가의 id
     * @return 조회된 사진작가
     * @throws PhotographerNotFoundException id와 일치하는 사진작가가 없는 경우
     */
    fun getById(id: Long): Photographer

    /**
     * 사진작가를 업데이트한다.
     *
     * @param photographer 업데이트하고자 하는 사진작가 정보가 담긴 객체
     * @return 수정된 사진작가
     */
    fun update(photographer: Photographer): Photographer
    fun delete(photographer: Photographer)
}
