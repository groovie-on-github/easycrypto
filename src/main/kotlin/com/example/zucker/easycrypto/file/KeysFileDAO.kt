package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.KeysDAO
import com.example.zucker.easycrypto.KeysDTO
import java.nio.file.Path

class KeysFileDAO(private val filePath: Path): KeysDAO {

    override fun save(dto: KeysDTO) = KeysFile(filePath).save(dto)

    override fun findOne(): KeysDTO = KeysFile(filePath).load()
}