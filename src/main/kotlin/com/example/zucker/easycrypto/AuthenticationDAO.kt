package com.example.zucker.easycrypto

interface AuthenticationDAO {

    fun save(dto: AuthenticationDTO)

    fun findOne(): AuthenticationDTO
}