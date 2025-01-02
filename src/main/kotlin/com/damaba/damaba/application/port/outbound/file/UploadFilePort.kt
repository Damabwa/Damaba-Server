package com.damaba.damaba.application.port.outbound.file

import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.UploadFile

interface UploadFilePort {
    /**
     * 파일 저장소에 파일을 업로드한다.
     *
     * @param file 업로드할 파일
     * @param path 파일을 저장할 위치
     * @return 업로드된 파일 정보
     */
    fun upload(file: UploadFile, path: String): File
}
