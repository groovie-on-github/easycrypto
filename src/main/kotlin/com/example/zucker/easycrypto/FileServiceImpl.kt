package com.example.zucker.easycrypto

import com.example.zucker.easycrypto.file.FileFormat
import java.nio.file.Files
import java.nio.file.Path

class FileServiceImpl: FileService {

    private val fileQueue = mutableListOf<Path>()


    override fun detectFileFormat(filePath: Path): FileFormat =
        try {
            Files.newInputStream(filePath).use {
                FileFormat.valueOf(it.readNBytes(6).decodeToString())
            }
        } catch (e: Exception) {
            FileFormat.UNKNWN
        }

    override fun enqueueFilePath(filePath: Path) { fileQueue.add(filePath) }

    override fun dequeueFilePath(): Path = fileQueue.removeFirst()
}