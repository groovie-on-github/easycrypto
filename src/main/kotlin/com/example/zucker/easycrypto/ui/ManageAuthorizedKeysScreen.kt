package com.example.zucker.easycrypto.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.zucker.easycrypto.ServiceFactory

@Composable
@Suppress("FunctionName")
fun ManageAuthorizedKeysScreen() {
    Column(
        modifier = Modifier.padding(15.dp).fillMaxSize()
    ) {

        val keyService = ServiceFactory.keyService
        var authorizedKeyNames by remember { mutableStateOf(keyService.getAuthorizedKeyNames()) }

        var showConfirmDialog by remember { mutableStateOf(false) }
        var confirmDialogMessage by remember { mutableStateOf("") }
        var confirmDialogOnConfirm by remember { mutableStateOf({}) }

        val spaceModifier = Modifier.padding(vertical = 8.dp)

        Text(
            modifier = spaceModifier,
            text = "承認済み公開鍵管理",
            style = MaterialTheme.typography.h5
        )

        Spacer(spaceModifier)

        Text("承認済み公開鍵一覧：")
        if (authorizedKeyNames.isNotEmpty()) {
            Card(Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color.Black)) {
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(15.dp, 0.dp)) {
                    items(authorizedKeyNames) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(it)
                            IconButton(
                                onClick = {
                                    showConfirmDialog = true
                                    confirmDialogMessage = "'$it'の公開鍵を削除します\n本当によろしいですか？"
                                    confirmDialogOnConfirm = {
                                        keyService.unregisterAuthorizedKey(it)
                                        authorizedKeyNames = keyService.getAuthorizedKeyNames()
                                        showConfirmDialog = false
                                }   },
                                enabled = !showConfirmDialog
                            ) { Icon(Icons.Default.Delete, "delete") }
        }   }   }   }   }
        else {
            Text("認証済みの公開鍵はありません", spaceModifier, color = Color.Red)
        }

        BackButton(!showConfirmDialog, spaceModifier) {}

        ConfirmDialog(showConfirmDialog, confirmDialogMessage, { showConfirmDialog = false }, confirmDialogOnConfirm)
}   }

@Composable
@Suppress("FunctionName")
fun ConfirmDialog(show: Boolean, message: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (show) {
        AlertDialog(
            properties = DialogProperties("確認", resizable = false),
            text = { Text(message, color = Color.Red) },
            onDismissRequest = onDismiss,
            confirmButton = { Button(onConfirm) { Text("OK") } },
            dismissButton = { Button(onDismiss) { Text("キャンセル") } }
        )
}   }