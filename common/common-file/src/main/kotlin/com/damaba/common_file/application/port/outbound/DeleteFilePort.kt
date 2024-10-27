package com.damaba.common_file.application.port.outbound

import com.damaba.common_file.domain.UploadedFile

interface DeleteFilePort {
    /**
     * 업로드 된 파일을 삭제합니다.
     *
     * @param file 삭제할 파일 정보
     */
    fun delete(file: UploadedFile)
}
