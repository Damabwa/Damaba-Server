package com.damaba.damaba.infrastructure.file

import com.damaba.damaba.domain.file.File
import com.damaba.damaba.domain.file.UploadFile

interface FileStorageManager {
    /**
     * 파일 저장소에 파일을 업로드한다.
     *
     * @param file 업로드할 파일
     * @param path 파일을 저장할 위치
     * @return 업로드된 파일 정보
     */
    fun upload(file: UploadFile, path: String): File

    /**
     * 파일 저장소에 파일을 업로드한다.
     *
     * @param files 업로드할 파일 리스트
     * @param path 파일을 저장할 위치
     * @return uploaded files
     */
    fun upload(files: List<UploadFile>, path: String): List<File>

    /**
     * 업로드 된 파일을 삭제합니다.
     *
     * @param file 삭제할 파일 정보
     */
    fun delete(file: File)
}
