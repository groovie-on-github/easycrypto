package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.ValidationException
import java.nio.file.Files
import java.nio.file.Path

class DroppedEncryptedFile(private val filePath: Path) {

    companion object {
        val ENCRYPT_FILE_EXTENSION = "." + FileFormat.ECENCF.name.toLowerCase()
        const val DECRYPT_FILE_PREFIX = "decrypted_"
    }

    val outputFilePath: Path by lazy {
        if (filePath.fileName.toString().endsWith(ENCRYPT_FILE_EXTENSION))
            filePath.resolveSibling(filePath.fileName.toString().removeSuffix(ENCRYPT_FILE_EXTENSION))
        else
            filePath.resolveSibling(DECRYPT_FILE_PREFIX + filePath.fileName.toString())
    }

    fun decrypt() = CryptoFile(outputFilePath, filePath).decrypt(ServiceFactory.keyService.getKeys().first)

    fun validate() {
        if (Files.exists(outputFilePath))
            throw ValidationException("ファイルが存在するため処理を中止しました[${outputFilePath.toAbsolutePath()}]")
    }
}