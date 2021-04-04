package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.file.DroppedPublicKeyFile
import com.example.zucker.easycrypto.file.PublicKeyFile

@Composable
@Suppress("FunctionName")
fun AuthorizeScreen(
    progressIndicator: (coroutineCallback: () -> Unit) -> Unit,
    errorDialog: (message: String, onClose: () -> Unit) -> Unit,
    popupMessage: (message: String, actionLabel: String, onAction: () -> Unit) -> Unit) {

    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {
        val filePath = remember { ServiceFactory.fileService.dequeueFilePath() }
        var name by remember { mutableStateOf("") }

        val publicKey = remember {
            try { PublicKeyFile(filePath).load() }
            catch(e: Exception) {
                e.printStackTrace()
                errorDialog("公開鍵の抽出に失敗しました") { AppState.currentScreen = ScreenType.DropArea }
                null
        }   }

        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = "公開鍵承認",
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

        Spacer(spaceModifier)

        OutlinedTextField(
            modifier = spaceModifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text("鍵の所有者") },
            placeholder = { Text("登録する名前") },
            singleLine = true
        )

        Button(
            modifier = spaceModifier.fillMaxWidth(),
            onClick = {
                val trimmedName = name.trim()
                val droppedPublicKey = DroppedPublicKeyFile(trimmedName, publicKey!!)
                progressIndicator {
                    runCatching {
                        droppedPublicKey.validate()
                    }.onSuccess {
                        droppedPublicKey.authorize()
                        popupMessage("'$trimmedName'の公開鍵を承認しました", "OK") {
                            AppState.currentScreen = ScreenType.DropArea
                        }
                    }.onFailure { errorDialog(it.message!!) {} }
            }   }
        ) {
            Text("承認する")
        }

        BackButton(enabled = true) {}
}   }