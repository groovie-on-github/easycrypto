package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.AuthenticationDTO
import java.nio.file.Files
import java.nio.file.Path

class AuthenticationFile(private val filePath: Path) {

    companion object {
        const val CURRENT_VERSION: Byte = 1
        const val BUFFER_SIZE = 1024 * 1024
    }

    fun save(dto: AuthenticationDTO) {
        Files.createDirectories(filePath.parent)
        Files.newOutputStream(filePath).buffered(BUFFER_SIZE).use { output ->
            val fileHeader = ByteArray(8).apply {
                FileFormat.ECAUTH.name.encodeToByteArray().copyInto(this)
                this[6] = CURRENT_VERSION
            }
            output.write(fileHeader)
            output.write(dto.hash)
            output.write(dto.salt)
    }   }

    fun load(): AuthenticationDTO {
        Files.newInputStream(filePath).buffered(BUFFER_SIZE).use { input ->
            val fileHeader = input.readNBytes(8)
            val fileFormat = fileHeader.sliceArray(0..5).decodeToString()
            if (FileFormat.valueOf(fileFormat) != FileFormat.ECAUTH) { TODO("implement throw exception") }
            // TODO "version check"
            val data = input.readAllBytes()
            return AuthenticationDTO(data.sliceArray(0 until data.size - 16),
                                     data.sliceArray((data.size - 16) until data.size))
        }
    }
}