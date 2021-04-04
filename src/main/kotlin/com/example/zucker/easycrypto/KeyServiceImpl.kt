package com.example.zucker.easycrypto

import com.example.zucker.easycrypto.file.PublicKeyFile
import java.security.*

class KeyServiceImpl(private val keysDAO: KeysDAO, private val authorizedKeyDAO: AuthorizedKeyDAO): KeyService {

    override fun generateKeyPair(): KeyPair =
        KeyPairGenerator.getInstance("RSA").apply { initialize(4096, SecureRandom.getInstanceStrong()) }
            .genKeyPair()

    override fun storeKeys(privateKey: PrivateKey, publicKey: PublicKey) = keysDAO.save(KeysDTO(privateKey, publicKey))

    override fun getKeys(): Pair<PrivateKey, PublicKey> = keysDAO.findOne().let { Pair(it.privateKey, it.publicKey) }

    override fun exportPublicKey(privateKey: PrivateKey, publicKey: PublicKey) =
        PublicKeyFile(AppState.PUBLIC_KEY_FILEPATH).save(privateKey, publicKey)

    override fun storeAuthorizedKeys() = authorizedKeyDAO.save()

    override fun registerAuthorizedKey(name: String, publicKey: PublicKey) =
        authorizedKeyDAO.save(AuthorizedKeyDTO(name, publicKey))

    override fun unregisterAuthorizedKey(name: String) =authorizedKeyDAO.delete(name)

    override fun getAuthorizedKey(name: String) =
        authorizedKeyDAO.findBy(name)?.let { Pair(it.name, it.publicKey) }

    override fun getAuthorizedKey(publicKey: PublicKey) =
        authorizedKeyDAO.findBy(publicKey)?.let { Pair(it.name, it.publicKey) }

    override fun getAuthorizedKeyNames(): List<String> =
        authorizedKeyDAO.findAll().map { it.name }.sorted()

    override fun getAuthorizedKeys(vararg names: String): List<PublicKey> =
        authorizedKeyDAO.findByNameIn(*names).map { it.publicKey }

    override fun refreshAuthorizedKeys() =
        authorizedKeyDAO.reload()
}