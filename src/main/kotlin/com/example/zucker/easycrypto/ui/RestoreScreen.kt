package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.ServiceFactory

@Composable
@Suppress("FunctionName")
fun RestoreScreen(
    progressIndicator: (coroutineCallback: () -> Unit) -> Unit,
    errorDialog: (message: String, onClose: () -> Unit) -> Unit,
    popupMessage: (message: String, actionLabel: String, onAction: () -> Unit) -> Unit) {

    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {
        val fileService = ServiceFactory.fileService
        val filePath = remember { fileService.dequeueFilePath() }
        var progressing by remember { mutableStateOf(false) }
        val toggleProgressing = { progressing = !progressing }
        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = "バックアップ復元",
            style = MaterialTheme.typography.h5
        )

        Spacer(spaceModifier)

        Text(
            modifier = spaceModifier,
            text = buildAnnotatedString {
                withStyle(style = ParagraphStyle()) { append("以下の設定が復元されます：\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("・パスワード\n")
                        append("・公開鍵とプライベート鍵のペア\n")
                        append("・承認した公開鍵")
            }   }   }
        )

        Button(
            modifier = Modifier.fillMaxWidth().then(spaceModifier),
            onClick = {
                toggleProgressing()
                progressIndicator {
                    runCatching {
                        AppState.restore(filePath)
                    }.onSuccess {
                        popupMessage("設定を復元しました", "OK") {
                            AppState.isLoggedIn = false
                            AppState.currentScreen = ScreenType.Login
                        }
                    }.onFailure {
                        it.printStackTrace()
                        errorDialog("設定の復元に失敗しました") {}
                    }
                    toggleProgressing()
            }   },
            enabled = !progressing
        ) { Text("復元する") }

        BackButton(!progressing, spaceModifier) {}
}   }
