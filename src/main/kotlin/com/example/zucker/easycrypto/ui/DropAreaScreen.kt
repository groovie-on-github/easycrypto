package com.example.zucker.easycrypto.ui

import androidx.compose.desktop.AppManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.zucker.easycrypto.AppState
import com.example.zucker.easycrypto.file.DroppedFile
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File

@Composable
@Suppress("FunctionName")
fun DropAreaScreen(errorDialog: (message: String, onClose: () -> Unit) -> Unit) {

    val contentPane = AppManager.windows.first().window.contentPane
    val dropTarget = object: DropTarget() {
        override fun drop(dtde: DropTargetDropEvent) {
            dtde.acceptDrop(DnDConstants.ACTION_LINK)
            val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
            val file = fileList.firstOrNull { (it as File).isFile }
            if (file != null) {
                runCatching {
                    DroppedFile(file as File).nextScreen()
                }
                .onSuccess { AppState.currentScreen = it }
                .onFailure { errorDialog(it.message!!) {} }
            }
            contentPane.dropTarget = null
    }   }
    contentPane.dropTarget = dropTarget

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ここにファイルをドロップ")
}   }
