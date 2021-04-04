package com.example.zucker.easycrypto

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import com.example.zucker.easycrypto.file.BackupFile
import java.nio.file.Files
import java.nio.file.Path

object AppState {

    private val DATA_DIR: Path = Path.of("data")

    val AUTHENTICATION_FILEPATH: Path = DATA_DIR.resolve(".authentication")

    val KEYS_FILEPATH: Path = DATA_DIR.resolve(".keys")

    val AUTHORIZED_KEYS_FILEPATH: Path = DATA_DIR.resolve(".authorized_keys")

    val PUBLIC_KEY_FILEPATH: Path = Path.of("public_key")

    val BACKUP_FILEPATH: Path = Path.of("easy-crypto.bak")

    val isInitialized
        get() = Files.exists(AUTHENTICATION_FILEPATH)

    var currentScreen by mutableStateOf(ScreenType.Login)

    var isLoggedIn by mutableStateOf(false)


    fun backup() {
        ServiceFactory.keyService.storeAuthorizedKeys()
        BackupFile(BACKUP_FILEPATH).save()
    }

    fun restore(filePath: Path) {
        BackupFile(filePath).load()
        ServiceFactory.keyService.refreshAuthorizedKeys()
    }
}