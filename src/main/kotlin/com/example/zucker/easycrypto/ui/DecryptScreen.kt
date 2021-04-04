package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.file.DroppedEncryptedFile

@Composable
@Suppress("FunctionName")
fun DecryptScreen(
    progressIndicator: (coroutineCallback: () -> Unit) -> Unit,
    errorDialog: (message: String, onClose: () -> Unit) -> Unit,
    popupMessage: (message: String, actionLabel: String, onAction: () -> Unit) -> Unit) {

    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {
        val fileService = ServiceFactory.fileService
        val filePath = remember { fileService.dequeueFilePath() }
        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = "ファイル復号化",
            style = MaterialTheme.typography.h5
        )

        Text(
            modifier = spaceModifier,
            text = buildAnnotatedString {
                withStyle(style = ParagraphStyle()) { append("ファイルの場所：") }
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(filePath.toAbsolutePath().toString())
            }   }
        )

        LaunchedEffect(filePath) {
            progressIndicator {
                val encryptedFile = DroppedEncryptedFile(filePath)
                runCatching {
                    encryptedFile.validate()
                }.onSuccess {
                    runCatching {
                        encryptedFile.decrypt()
                    }.onSuccess {
                        popupMessage(
                            "復号化したファイルを'${encryptedFile.outputFilePath.toAbsolutePath()}'に出力しました",
                            "OK"
                        ) { AppState.currentScreen = ScreenType.DropArea }
                    }.onFailure {
                        it.printStackTrace()
                        errorDialog(if (it is IllegalStateException) it.message!! else "復号化に失敗しました") {
                            AppState.currentScreen = ScreenType.DropArea
                    }   }
                }.onFailure {
                    errorDialog(it.message!!) { AppState.currentScreen = ScreenType.DropArea }
                }
}   }   }   }
