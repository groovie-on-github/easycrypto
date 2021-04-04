package com.example.zucker.easycrypto

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AuthenticationServiceImpl(private val dao: AuthenticationDAO): AuthenticationService {


    override fun register(password: CharArray) {
        val salt = ByteArray(16).apply { SecureRandom.getInstanceStrong().nextBytes(this) }
        dao.save(AuthenticationDTO(computeHash(password, salt), salt))
        password.fill('\u0000')
    }

    override fun authenticate(password: CharArray): Boolean {
        val dto = dao.findOne()
        return computeHash(password, dto.salt).contentEquals(dto.hash).also {
            password.fill('\u0000')
    }   }


    private fun computeHash(password: CharArray, salt: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(password, salt, 4096, 256)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec).encoded.also {
            keySpec.clearPassword()
    }   }
}