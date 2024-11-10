package com.damaba.file.application.port.outbound

import com.damaba.common_file.domain.File
import com.damaba.file.domain.UploadFile

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
