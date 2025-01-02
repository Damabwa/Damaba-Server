package com.damaba.damaba.application.port.outbound.file

import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.UploadFile

interface UploadFilesPort {
    /**
     * 파일 저장소에 파일을 업로드한다.
     *
     * @param files 업로드할 파일 리스트
     * @param path 파일을 저장할 위치
     * @return uploaded files
     */
    fun upload(files: List<UploadFile>, path: String): List<File>
}
