package com.example.zucker.easycrypto

interface KeysDAO {

    fun save(dto: KeysDTO)

    fun findOne(): KeysDTO
}