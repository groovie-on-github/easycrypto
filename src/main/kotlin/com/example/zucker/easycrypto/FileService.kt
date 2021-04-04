package com.example.zucker.easycrypto

import com.example.zucker.easycrypto.file.FileFormat
import java.nio.file.Path

interface FileService {

    fun detectFileFormat(filePath: Path): FileFormat

    fun enqueueFilePath(filePath: Path)

    fun dequeueFilePath(): Path
}