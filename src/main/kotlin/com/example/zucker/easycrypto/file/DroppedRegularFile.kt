package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.ValidationException
import java.nio.file.Files
import java.nio.file.Path

class DroppedRegularFile(private val filePath: Path, private val decryptableKeyNames: List<String>) {

    companion object {
        val ENCRYPT_FILE_EXTENSION = "." + FileFormat.ECENCF.name.toLowerCase()
    }

    val outputFilePath: Path = filePath.resolveSibling(filePath.fileName.toString() + ENCRYPT_FILE_EXTENSION)


    fun encrypt(names: List<String>) = CryptoFile(filePath, outputFilePath)
                                        .encrypt(ServiceFactory.keyService.getAuthorizedKeys(*names.toTypedArray()))

    fun validate() {
        if (decryptableKeyNames.isEmpty())
            throw ValidationException("暗号化を解除できる人を選択してください")

        if (Files.exists(outputFilePath) && !outputFilePath.toFile().canWrite())
            throw ValidationException("ファイルを上書きできません[${outputFilePath.toAbsolutePath()}]")
    }
}