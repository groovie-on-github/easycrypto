package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.ServiceFactory
import java.io.File

class DroppedFile(private val file: File) {

    fun nextScreen(): ScreenType {
        val fileService = ServiceFactory.fileService
        val filePath = file.toPath()
        val fileFormat = fileService.detectFileFormat(filePath)
        fileService.enqueueFilePath(filePath)

        return when (fileFormat) {
            FileFormat.ECPUBK -> ScreenType.Authorize
            FileFormat.ECENCF -> ScreenType.Decrypt
            FileFormat.ECBKDT -> ScreenType.Restore
            else -> {
                if (ServiceFactory.keyService.getAuthorizedKeyNames().isEmpty()) {
                    fileService.dequeueFilePath()
                    throw IllegalStateException("承認された公開鍵が存在しないためファイルの暗号化はできません\n承認する公開鍵をドロップしてください")
                }
                ScreenType.Encrypt
    }   }   }
}