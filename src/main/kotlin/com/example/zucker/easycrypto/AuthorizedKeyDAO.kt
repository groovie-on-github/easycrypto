package com.example.zucker.easycrypto

import java.security.PublicKey

interface AuthorizedKeyDAO {

    fun save()

    fun save(dto: AuthorizedKeyDTO)

    fun load(): List<AuthorizedKeyDTO>

    fun findAll(): List<AuthorizedKeyDTO>

    fun findBy(name: String): AuthorizedKeyDTO?

    fun findBy(publicKey: PublicKey): AuthorizedKeyDTO?

    fun findByNameIn(vararg names: String): List<AuthorizedKeyDTO>

    fun delete(name: String)

    fun reload()
}