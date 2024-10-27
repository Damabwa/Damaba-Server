package com.damaba.user.application.port.outbound.file

import com.damaba.user.domain.file.UploadedFile

interface DeleteFilePort {
    /**
     * 업로드 된 파일을 삭제합니다.
     *
     * @param file 삭제할 파일 정보
     */
    fun delete(file: UploadedFile)
}
