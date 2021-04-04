package com.example.zucker.easycrypto

import com.example.zucker.easycrypto.file.AuthenticationFileDAO
import com.example.zucker.easycrypto.file.AuthorizedKeyFileDAO
import com.example.zucker.easycrypto.file.KeysFileDAO
import java.nio.file.Files

object ServiceFactory {

    val authenticationService: AuthenticationService by lazy {
        AuthenticationServiceImpl(AuthenticationFileDAO(AppState.AUTHENTICATION_FILEPATH))
    }

    val keyService: KeyService by lazy {
        val authorizedKeyFileDAO = AuthorizedKeyFileDAO(AppState.AUTHORIZED_KEYS_FILEPATH)
        if (Files.exists(AppState.AUTHORIZED_KEYS_FILEPATH)) authorizedKeyFileDAO.load()

        KeyServiceImpl(KeysFileDAO(AppState.KEYS_FILEPATH), authorizedKeyFileDAO)
    }

    val fileService: FileService by lazy {
        FileServiceImpl()
    }
}