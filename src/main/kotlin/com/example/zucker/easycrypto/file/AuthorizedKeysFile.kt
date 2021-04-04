package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.AuthorizedKeyDTO
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.Path

class AuthorizedKeysFile(private val filePath: Path) {

    companion object {
        const val CURRENT_VERSION: Byte = 1
        const val BUFFER_SIZE = 1024 * 1024
    }

    fun save(authorizedKeys: List<AuthorizedKeyDTO>) {
        Files.newOutputStream(filePath).buffered(BUFFER_SIZE).use { output ->
            val fileHeader = ByteArray(8).apply {
                FileFormat.ECAKEY.name.encodeToByteArray().copyInto(this)
                this[6] = CURRENT_VERSION
            }
            output.write(fileHeader)
        ObjectOutputStream(output).use { objOut ->
            objOut.writeObject(authorizedKeys)
    }   }}

    fun load(): List<*> {
        Files.newInputStream(filePath).buffered(BUFFER_SIZE).use { input ->
            val fileHeader = input.readNBytes(8)
            val fileFormat = fileHeader.sliceArray(0..5).decodeToString()
            if (FileFormat.valueOf(fileFormat) != FileFormat.ECAKEY) { TODO("implement throw exception") }
            // TODO "version check"
        ObjectInputStream(input).use { objIn ->
            return objIn.readObject() as List<*>
    }   }}
}