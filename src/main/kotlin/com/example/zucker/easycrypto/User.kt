package com.example.zucker.easycrypto
class User(private val password: String, private val confirmation: String, val isInitialized: Boolean) {

    fun initialize(onSuccess: () -> Unit, onFailure: () -> Unit) {
        runCatching {
            ServiceFactory.authenticationService.register(password.toCharArray())
            with(ServiceFactory.keyService) {
                generateKeyPair().also {
                    storeKeys(it.private, it.public)
                    exportPublicKey(it.private, it.public)
                }
                storeAuthorizedKeys()
        }   }
        .onSuccess { onSuccess() }
        .onFailure {
            it.printStackTrace()
            onFailure()
    }   }

    fun authenticate(onSuccess: () -> Unit, onFailure: () -> Unit) {
        runCatching {
            ServiceFactory.authenticationService.authenticate(password.toCharArray())
        }
        .onSuccess { if (it) onSuccess() else onFailure() }
        .onFailure {
            it.printStackTrace()
            onFailure()
    }   }

    fun validate() {
        when {
            password.isEmpty() -> throw ValidationException("パスワードが入力されていません")
            !isInitialized && password != confirmation -> throw ValidationException("パスワードが一致しません")
    }   }
}