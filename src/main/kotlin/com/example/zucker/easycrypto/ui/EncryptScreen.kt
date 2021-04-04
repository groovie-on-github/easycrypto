package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.ServiceFactory
import com.example.zucker.easycrypto.file.DroppedRegularFile

@Composable
@Suppress("FunctionName")
fun EncryptScreen(
    progressIndicator: (coroutineCallback: () -> Unit) -> Unit,
    errorDialog: (message: String, onClose: () -> Unit) -> Unit,
    popupMessage: (message: String, actionLabel: String, onAction: () -> Unit) -> Unit) {

    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {
        val keyService = ServiceFactory.keyService
        val fileService = ServiceFactory.fileService
        val filePath = remember { fileService.dequeueFilePath() }

        val targetNames = remember { mutableListOf<String>() }

        var progressing by remember { mutableStateOf(false) }
        val toggleProgressing = { progressing = !progressing }

        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = "ファイル暗号化",
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

        Text("暗号化を解除できる人を選択：")
        Card(border = BorderStroke(1.dp, Color.Black)) {
            LazyColumn(Modifier.heightIn(max = 150.dp).padding(5.dp)) {
                items(keyService.getAuthorizedKeyNames()) {
                    CheckableText(it, !progressing) { checked ->
                        if (checked) targetNames.add(it) else targetNames.remove(it)
        }   }   }   }

        Button(
            onClick = {
                toggleProgressing()
                progressIndicator {
                    val regularFile = DroppedRegularFile(filePath, targetNames)
                    runCatching {
                        regularFile.validate()
                    }.onSuccess {
                        runCatching {
                            regularFile.encrypt(targetNames)
                        }.onSuccess {
                            popupMessage(
                                "暗号化したファイルを'${regularFile.outputFilePath.toAbsolutePath()}'に出力しました",
                                "OK"
                            ) { AppState.currentScreen = ScreenType.DropArea }
                        }.onFailure {
                            it.printStackTrace()
                            errorDialog("暗号化に失敗しました") { AppState.currentScreen = ScreenType.DropArea }
                        }
                    }.onFailure {
                        errorDialog(it.message!!) {}
                    }
                    toggleProgressing()
            }   },
            modifier = Modifier.fillMaxWidth().then(spaceModifier),
            enabled = !progressing
        ) {
            Text("暗号化する")
        }

        BackButton(!progressing, spaceModifier) {}
}   }

@Composable
@Suppress("FunctionName")
fun CheckableText(text: String, enable: Boolean,onChange: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    Row(Modifier.padding(vertical = 2.dp).clickable {
        if (enable) {
            isChecked = !isChecked
            onChange(isChecked)
        }   },
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.CheckCircle, "check",
            Modifier.padding(end = 5.dp),
            if (isChecked) MaterialTheme.colors.primarySurface else Color.LightGray)
        Text(text, Modifier.fillMaxWidth())
}   }