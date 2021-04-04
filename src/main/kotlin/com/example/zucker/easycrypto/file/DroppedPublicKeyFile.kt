package com.example.zucker.easycrypto.file

import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.ValidationException
import java.security.PublicKey

class DroppedPublicKeyFile(private val name: String, private val publicKey: PublicKey) {

    fun authorize() = ServiceFactory.keyService.registerAuthorizedKey(name, publicKey)

    fun validate() {
        val keyService = ServiceFactory.keyService
        when {
            name.isEmpty() -> throw ValidationException("登録名が入力されていません")
            keyService.getAuthorizedKey(name) != null ->
                throw ValidationException("登録名'$name'は既に使用されています")
            else -> {
                val authorizedKey = keyService.getAuthorizedKey(publicKey)
                if (authorizedKey != null)
                    throw ValidationException("この鍵は'${authorizedKey.first}'として既に登録されています")
    }   }   }
}