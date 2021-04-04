package com.example.zucker.easycrypto

import androidx.compose.desktop.AppManager
import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize
import com.example.zucker.easycrypto.ui.MainWindowContent
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

fun main() {
    AppManager.setEvents(
        onAppStart = { Security.addProvider(BouncyCastleProvider()) },
        onAppExit = { ServiceFactory.keyService.storeAuthorizedKeys() }
    )

    Window(
        title = "EasyCrypto for Desktop",
        size = IntSize(400, 600)
    ) {
        MainWindowContent()
}   }
