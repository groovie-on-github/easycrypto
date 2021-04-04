package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.AuthenticationDAO
import com.example.zucker.easycrypto.AuthenticationDTO
import java.nio.file.Path

class AuthenticationFileDAO(private val filePath: Path): AuthenticationDAO {

    override fun save(dto: AuthenticationDTO) = AuthenticationFile(filePath).save(dto)

    override fun findOne(): AuthenticationDTO = AuthenticationFile(filePath).load()
}