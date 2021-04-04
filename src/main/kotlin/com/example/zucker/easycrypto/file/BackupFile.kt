package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.AppState
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupFile(private val filePath: Path) {

    companion object {
        const val CURRENT_VERSION: Byte = 1
        const val BUFFER_SIZE = 1024 * 1024
    }

    private val fileMap = mapOf(
        FileFormat.ECAUTH.name to AppState.AUTHENTICATION_FILEPATH,
        FileFormat.ECKEYS.name to AppState.KEYS_FILEPATH,
        FileFormat.ECAKEY.name to AppState.AUTHORIZED_KEYS_FILEPATH
    )

    fun save() {
        Files.newOutputStream(filePath).buffered(BUFFER_SIZE).use { output ->
            val fileHeader = ByteArray(8).apply {
                FileFormat.ECBKDT.name.encodeToByteArray().copyInto(this)
                this[6] = CURRENT_VERSION
            }
            output.write(fileHeader)

        ZipOutputStream(output).use { zipOut ->
            zipOut.setLevel(Deflater.BEST_COMPRESSION)
            fileMap.forEach { (format, path) ->
                zipOut.putNextEntry(ZipEntry(format))
                Files.newInputStream(path).buffered(BUFFER_SIZE).use { input -> input.copyTo(zipOut) }
    }   }}  }

    fun load() {
        val moveFileMap = fileMap.mapValues { it.value.resolveSibling(it.value.fileName.toString() + ".bak") }

        try {
            Files.newInputStream(filePath).buffered(BUFFER_SIZE).use { input ->
                val fileHeader = input.readNBytes(8)
                val fileFormat = fileHeader.sliceArray(0..5).decodeToString()
                if (FileFormat.valueOf(fileFormat) != FileFormat.ECBKDT) { TODO("implement throw exception") }
                // TODO "version check"
            ZipInputStream(input).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    Files.move(fileMap.getValue(entry.name), moveFileMap.getValue(entry.name))
                    Files.newOutputStream(fileMap.getValue(entry.name)).buffered(BUFFER_SIZE).use { output -> zipIn.copyTo(output) }
                    entry = zipIn.nextEntry
            }}  }
        } catch (e: Exception) {
            moveFileMap.forEach { (key, path) ->
                if (Files.exists(path)) { Files.move(path, fileMap.getValue(key), StandardCopyOption.REPLACE_EXISTING) }
            }
        } finally {
            moveFileMap.forEach { (_, path) -> Files.deleteIfExists(path) }
}   }   }