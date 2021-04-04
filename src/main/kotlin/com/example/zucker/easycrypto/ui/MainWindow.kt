package com.example.zucker.easycrypto.ui

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.zucker.easycrypto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Suppress("FunctionName")
fun MainWindowContent() {
    DesktopMaterialTheme(
        colors = lightColors()
    ) {
        val snackbarHostState = SnackbarHostState()
        val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

        val composableScope = rememberCoroutineScope()
        var progressing by remember { mutableStateOf(false) }
        val progressIndicator: (() -> Unit) -> Unit = { coroutineCallback ->
            progressing = true
            composableScope.launch(Dispatchers.Default) {
                coroutineCallback()
                progressing = false
            }   }

        var hasError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var errorOnClose by remember { mutableStateOf({}) }
        val errorDialog: (String, () -> Unit) -> Unit = { message, onClose ->
            hasError = true
            errorMessage = message
            errorOnClose = { composableScope.launch(Dispatchers.Default) { onClose() }; hasError = false }
        }

        var showSnack by remember { mutableStateOf(false) }
        var snackMessage by remember { mutableStateOf("") }
        var snackActionLabel by remember { mutableStateOf("OK") }
        var snackOnAction by remember { mutableStateOf({}) }
        val popupMessage: (String, String, () -> Unit) -> Unit = { message, actionLabel, onAction ->
            showSnack = true
            snackMessage = message
            snackActionLabel = if (actionLabel.isBlank()) "OK" else actionLabel
            snackOnAction = { composableScope.launch(Dispatchers.Default) { onAction() }; showSnack = false }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text("EasyCrypto") }, actions = { AppBarMenu(errorDialog, popupMessage) }) },
            scaffoldState = scaffoldState
        ){
            Box(Modifier.fillMaxSize()) {
                when (AppState.currentScreen) {
                    ScreenType.Login -> LoginScreen(progressIndicator, errorDialog)
                    ScreenType.DropArea -> DropAreaScreen(errorDialog)
                    ScreenType.Authorize -> AuthorizeScreen(progressIndicator, errorDialog, popupMessage)
                    ScreenType.Encrypt -> EncryptScreen(progressIndicator, errorDialog, popupMessage)
                    ScreenType.Decrypt -> DecryptScreen(progressIndicator, errorDialog, popupMessage)
                    ScreenType.ManageAuthorizedKeys -> ManageAuthorizedKeysScreen()
                    ScreenType.Restore -> RestoreScreen(progressIndicator, errorDialog, popupMessage)
                }
                ProgressIndicator(this, progressing)
                ErrorDialog(hasError, errorMessage, errorOnClose)
                PopupMessage(snackbarHostState, showSnack, snackMessage, snackActionLabel, snackOnAction)
}   }   }   }


@Composable
@Suppress("FunctionName")
fun ProgressIndicator(boxScope: BoxScope, progressing: Boolean) {
    if (progressing) {
        boxScope.apply {
            Column(Modifier.fillMaxSize().background(SolidColor(Color.Gray), alpha = 0.5f)) {}
            CircularProgressIndicator(Modifier.align(Alignment.Center))
}   }   }

@Composable
@Suppress("FunctionName")
fun ErrorDialog(show: Boolean, message: String, onClose: () -> Unit) {
    if (show) {
        AlertDialog(
            properties = DialogProperties("エラー", resizable = false),
            text = { Text(message, color = Color.Red) },
            onDismissRequest = onClose,
            confirmButton = { Button(onClose) { Text("閉じる") } }
        )
}   }

@Composable
@Suppress("FunctionName")
fun PopupMessage(snackbarHostState: SnackbarHostState, show: Boolean, message: String, actionLabel: String = "OK", onAction: () -> Unit) {
    if (show) {
        LaunchedEffect(Unit) {
            when (snackbarHostState.showSnackbar(message, actionLabel, SnackbarDuration.Indefinite)) {
                SnackbarResult.ActionPerformed -> onAction()
                SnackbarResult.Dismissed -> onAction()
}   }   }   }

@Composable
@Suppress("FunctionName")
fun AppBarMenu(errorDialog: (String, () -> Unit) -> Unit, popupMessage: (String, String, () -> Unit) -> Unit) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    if (AppState.isLoggedIn) {
        IconButton(onClick = { showDropdownMenu = true }) {
            Icon(Icons.Default.MoreVert, "MoreVert")
        }
        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false },
        ) {
            DropdownMenuItem(onClick = {
                runCatching {
                    with(ServiceFactory.keyService) { getKeys().also { exportPublicKey(it.first, it.second) } }
                }.onSuccess {
                    popupMessage("公開鍵を'${AppState.PUBLIC_KEY_FILEPATH.toAbsolutePath()}'に出力しました", "OK") {}
                }.onFailure {
                    it.printStackTrace()
                    errorDialog("公開鍵を出力できませんでした[${AppState.PUBLIC_KEY_FILEPATH.toAbsolutePath()}]") {}
                }
                showDropdownMenu = false
            }) {
                Text("公開鍵エクスポート")
            }
            DropdownMenuItem(onClick = {
                AppState.currentScreen = ScreenType.ManageAuthorizedKeys
                showDropdownMenu = false
            }) {
                Text("承認済み鍵管理")
            }
            DropdownMenuItem(onClick = {
                runCatching {
                    AppState.backup()
                }.onSuccess {
                    popupMessage("バックアップファイルを'${AppState.BACKUP_FILEPATH.toAbsolutePath()}'に出力しました", "OK") {}
                }.onFailure {
                    it.printStackTrace()
                    errorDialog("バックアップファイルを出力できませんでした[${AppState.BACKUP_FILEPATH.toAbsolutePath()}]") {}
                }
                showDropdownMenu = false
            }) {
                Text("バックアップ")
            }
}   }   }

@Composable
@Suppress("FunctionName")
fun BackButton(enabled: Boolean, modifier: Modifier = Modifier, nextScreen: ScreenType = ScreenType.DropArea, onClick: () -> Unit) {
    Spacer(Modifier.padding(vertical = 8.dp))
    Button(
        onClick = { onClick(); AppState.currentScreen = nextScreen },
        modifier = Modifier.fillMaxWidth().then(modifier),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondary),
        enabled = enabled) {
        Text("戻る")
}   }