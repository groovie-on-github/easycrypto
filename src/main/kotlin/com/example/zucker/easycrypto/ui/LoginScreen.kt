package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.ScreenType
import com.example.zucker.easycrypto.User

@Composable
@Suppress("FunctionName")
fun LoginScreen(
    progressIndicator: (coroutineCallback: () -> Unit) -> Unit,
    errorDialog: (message: String, onClose: () -> Unit) -> Unit) {

    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {
        var password by remember { mutableStateOf("") }
        var passwordConfirmation by remember { mutableStateOf("") }
        var progressing by remember { mutableStateOf(false) }
        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = if (AppState.isInitialized) "ログイン" else "ログイン設定",
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.padding(vertical = 8.dp).then(spaceModifier))

        PasswordTextField(password, "パスワード", spaceModifier, !progressing) { password = it }

        if (!AppState.isInitialized)
            PasswordTextField(passwordConfirmation, "パスワード(確認用)", spaceModifier, !progressing) {
                passwordConfirmation = it
            }

        Button(
            modifier = Modifier.fillMaxWidth().then(spaceModifier),
            enabled = !progressing,
            onClick = {
                val user = User(password, passwordConfirmation, AppState.isInitialized)
                progressing = true
                progressIndicator {
                    runCatching {
                        user.validate()
                    }.onSuccess {
                        if (user.isInitialized) {
                            user.authenticate(
                                {
                                    AppState.isLoggedIn = true
                                    AppState.currentScreen = ScreenType.DropArea
                                },
                                { errorDialog("認証できませんでした") {} })
                        } else {
                            user.initialize(
                                {
                                    AppState.isLoggedIn = true
                                    AppState.currentScreen = ScreenType.DropArea
                                },
                                { errorDialog("データの登録に失敗しました") {} })
                        }
                    }.onFailure {
                        errorDialog(it.message!!) {}
                    }
                    progressing = false
            }   }
        ) {
            Text(if (AppState.isInitialized) "ログイン" else "登録する")
}   }   }

@Composable
@Suppress("FunctionName")
fun PasswordTextField(value: String, label: String, spaceModifier: Modifier, enabled: Boolean, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().then(spaceModifier)
//                        .onKeyEvent { e ->
//                        if (e.key == Key.Enter && e.type == KeyEventType.KeyUp) println("Enter Key Up")
//                        true
//                        }
        ,
        label = { Text(label) },
        placeholder = { Text(label) },
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        enabled = enabled
    )
}
