package com.damaba.user.application.port.outbound.file

import com.damaba.user.domain.file.UploadFile
import com.damaba.user.domain.file.UploadedFile

interface UploadFilePort {
    /**
     * 파일 저장소에 파일을 업로드한다.
     *
     * @param file 업로드할 파일
     * @param path 파일을 저장할 위치
     * @return 업로드된 파일 정보
     */
    fun upload(file: UploadFile, path: String): UploadedFile
}
