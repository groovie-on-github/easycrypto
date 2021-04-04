package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.AuthorizedKeyDAO
import com.example.zucker.easycrypto.AuthorizedKeyDTO
import java.nio.file.Path
import java.security.PublicKey

class AuthorizedKeyFileDAO(private val filePath: Path): AuthorizedKeyDAO {

    private val authorizedKeys = mutableListOf<AuthorizedKeyDTO>()


    override fun save() = AuthorizedKeysFile(filePath).save(authorizedKeys)

    override fun save(dto: AuthorizedKeyDTO) {
        authorizedKeys.add(dto)
    }

    override fun load(): List<AuthorizedKeyDTO> {
        try {
            authorizedKeys.addAll(AuthorizedKeysFile(filePath).load().map { it as AuthorizedKeyDTO })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return authorizedKeys
    }

    override fun findAll(): List<AuthorizedKeyDTO> {
        return authorizedKeys
    }

    override fun findBy(name: String): AuthorizedKeyDTO? {
        return authorizedKeys.firstOrNull { it.name == name }
    }

    override fun findBy(publicKey: PublicKey): AuthorizedKeyDTO? {
        return authorizedKeys.firstOrNull { it.publicKey == publicKey }
    }

    override fun findByNameIn(vararg names: String): List<AuthorizedKeyDTO> {
        return authorizedKeys.filter { it.name in names }
    }

    override fun delete(name: String) {
        authorizedKeys.removeIf { it.name == name }
    }

    override fun reload() {
        authorizedKeys.clear()
        load()
    }
}