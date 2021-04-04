package com.example.zucker.easycrypto

import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

interface KeyService {

    fun generateKeyPair(): KeyPair

    fun storeKeys(privateKey: PrivateKey, publicKey: PublicKey)

    fun getKeys(): Pair<PrivateKey, PublicKey>

    fun exportPublicKey(privateKey: PrivateKey, publicKey: PublicKey)

    fun storeAuthorizedKeys()

    fun registerAuthorizedKey(name: String, publicKey: PublicKey)

    fun unregisterAuthorizedKey(name: String)

    fun getAuthorizedKey(name: String): Pair<String, PublicKey>?

    fun getAuthorizedKey(publicKey: PublicKey): Pair<String, PublicKey>?

    fun getAuthorizedKeyNames(): List<String>

    fun getAuthorizedKeys(vararg names: String): List<PublicKey>

    fun refreshAuthorizedKeys()
}