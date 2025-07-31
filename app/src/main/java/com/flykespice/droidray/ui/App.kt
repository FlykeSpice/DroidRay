package com.flykespice.droidray.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import com.flykespice.droidray.AppState
import com.flykespice.droidray.POVRay
import com.flykespice.droidray.R
import com.flykespice.droidray.ui.dialog.RenderOptionsDialog
import com.flykespice.droidray.ui.theme.DroidRayTheme
import kotlinx.coroutines.delay
import java.nio.ByteOrder

@Composable
fun POVRayApp(
    onSaveBitmap: (Bitmap) -> Unit,
    onClickOpen: () -> Unit,
    onClickSave: () -> Unit
) {
    val context = LocalContext.current
    var isRendering by remember { mutableStateOf((POVRay.getStatus(false) and POVRay.stRenderStartup) != 0) }
    var showLogBadge by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    if (errorMessage != null) {
        AlertDialog(
            title = { Text("Error") },
            text = { Text(errorMessage!!, textAlign = TextAlign.Center) },
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("Confirm") } }
        )
    }

    var newDialog by remember { mutableStateOf(false) }
    if (newDialog) {
        AlertDialog(
            title = { Text("New") },
            text = { Text("This will erase all your current work\nproceed?", textAlign = TextAlign.Center) },
            confirmButton = { TextButton(onClick = { newDialog = false; AppState.povCode = ""; }) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = { newDialog = false }) { Text("Cancel") } },
            onDismissRequest = { newDialog = false }
        )
    }

    var width by  remember { mutableIntStateOf(AppState.width) }
    var height by remember { mutableIntStateOf(AppState.height) }
    var antialias by remember { mutableStateOf(false) }
    var consoleLog by remember { mutableStateOf(listOf<POVRay.Message>()) }
    var renderDialog by remember { mutableStateOf(false) }
    if (renderDialog) {
        RenderOptionsDialog(
            width = width,
            height = height,
            antialias = antialias,
            onConfirm = { w, h, a ->
                width = w
                height = h
                antialias = a

                renderDialog = false
                context.openFileOutput("temp.pov", Context.MODE_PRIVATE).use {
                    it.write(AppState.povCode.toByteArray())
                }

                val error = POVRay.renderScene(
                    "${context.filesDir}/temp.pov",
                    "+L${context.filesDir}/include +W$width +H$height -F ${if (antialias) "+A" else "-A"}"
                )
                if (error != POVRay.vfeNoError) {
                    errorMessage = POVRay.getErrorString(error)
                    showLogBadge = true
                } else {
                    consoleLog = listOf()
                    isRendering = true
                }
            },
            onCancel = { renderDialog = false }
        )
    }

    LaunchedEffect(isRendering) {
        if (!isRendering)
            return@LaunchedEffect

        //It means we're rendering something
        while (true) {
            val status = POVRay.getStatus(false)
            if ((status and POVRay.stRenderShutdown) != 0) {
                if ((status and POVRay.stFailed) != 0)
                    errorMessage = "Render failed somehow (stFailed)\nCheck the Log for details"

                POVRay.getStatus(true)
                isRendering = false
                showLogBadge = true
                return@LaunchedEffect
            }
            delay(350)
        }
    }

    var destination by remember { mutableIntStateOf(0) }
    val editorScrollState = rememberScrollState()
    Scaffold(
        topBar = {
            AppTopBar(
                onClickNew = { newDialog = true },
                onClickOpen = onClickOpen,
                onClickSave = onClickSave
            )
        },
        bottomBar = {
            AppBottomNavigation(
                currentDestination = destination,
                onChangeDestination = {
                    destination = it
                    if (destination == 2) showLogBadge = false
                },
                showLogBadge = showLogBadge
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (isRendering) {
                    val err = POVRay.cancelRender()
                    if (err != POVRay.vfeNoError) {
                        errorMessage = POVRay.getErrorString(err)
                    }
                    isRendering = false
                } else {
                    renderDialog = true
                }
            }) {
                if (!isRendering)
                    Icon(Icons.Default.PlayArrow, "Render")
                else
                    Icon(painterResource(R.drawable.stop), "Cancel Render")
            }
        }
    ) { paddingValues ->

        AnimatedContent(
            targetState = destination,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            }
        ) { dest ->
            when (dest) {
                0 ->  { //editor
                    var textField by remember { mutableStateOf(TextFieldValue(AppState.povCode)) }
                    val commentRegex = remember { Regex("/\\*.*?\\*/") } //To speedup character input processing

                    LaunchedEffect(AppState.povCode) {
                        if (textField.text != AppState.povCode) {
                            textField = TextFieldValue(AppState.povCode)
                        }
                    }

                    EditorScreen(
                        textField = textField,
                        scrollState = editorScrollState,
                        onValueChange = {
                            var newText = it
                            //Was a new character added?
                            if (newText.text.length == textField.text.length+1) {
                                val cursorPos = newText.selection.start
                                val lastChar = newText.text[cursorPos-1]
                                when (lastChar) {
                                    '{' -> {
                                        if (newText.text.getOrNull(cursorPos) != '}') {
                                            newText = it.copy(
                                                text = it.text.replaceRange(
                                                    cursorPos - 1..cursorPos - 1,
                                                    "{}"
                                                )
                                            )
                                        }
                                    }

                                    '}' -> {
                                        //TODO: indent all the previous lines until the first '{'...
                                    }

                                    '<' -> {
                                        if (newText.text.getOrNull(cursorPos) != '>') {
                                            newText = it.copy(
                                                text = it.text.replaceRange(
                                                    cursorPos - 1..cursorPos - 1,
                                                    "<>"
                                                )
                                            )
                                        }
                                    }

                                    '\n' -> {
                                        val lastLine = it.text.substring(0..cursorPos-2)
                                            .substringAfterLast('\n')
                                            .substringBefore("//") //skip comments
                                            .replace(commentRegex, "")
                                        var indent = lastLine.takeWhile { it.isWhitespace() && it != '\n' }

                                        //TODO: Check if cursor inside brackets on the same line
                                        if ('{' in lastLine)
                                            indent += " ".repeat(4)

                                        newText = it.copy(
                                            text = it.text.replaceRange(
                                                cursorPos - 1..cursorPos - 1,
                                                "\n" + indent
                                            ),
                                            selection = TextRange((cursorPos - 1) + indent.length + 1)
                                        )

                                        val nextLine = newText.text
                                            .substring(cursorPos)
                                            .substringBefore('\n')
                                            .substringBefore("//")
                                            .replace(commentRegex, "")

                                        if ('}' in nextLine) {
                                            val index = newText.text.indexOf('}', cursorPos)
                                            newText = newText.copy(text = newText.text.replaceRange(index..index, "\n"+indent.dropLast(4)+"}"))
                                        }
                                    }
                                }
                            }

                            textField = newText
                            AppState.povCode = newText.text
                        },
                        styleText = { AnnotatedString(it) },
                        paddingValues = paddingValues
                    )
                }

                1 -> { //preview
                    var previewBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
                    val bitmap = remember(width, height) {
                        createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    }

                    LaunchedEffect(true) {
                        while (true) {
                            POVRay.imageBuffer.rewind()
                            POVRay.imageBuffer.order(ByteOrder.nativeOrder())
                            bitmap.copyPixelsFromBuffer(POVRay.imageBuffer)
                            previewBitmap = bitmap.asImageBitmap()
                            delay(400)
                        }
                    }

                    if (previewBitmap != null) {
                        Surface(Modifier.padding(paddingValues)) {
                            RenderPreviewScreen(
                                previewBitmap = previewBitmap!!,
                                isRendering = isRendering,
                                onClickSave = { onSaveBitmap(bitmap) }
                            )
                        }
                    }
                }

                2 -> { //Log
                    LaunchedEffect(true) {
                        while (true) {
                            val messages = POVRay.getMessages()
                            if (messages.isNotEmpty()) {
                                consoleLog += messages
                            }
                            delay(500)
                        }
                    }

                    Surface(Modifier.padding(paddingValues)) {
                        LogScreen(consoleLog)
                    }
                }
            }
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    onClickNew:  () -> Unit,
    onClickOpen: () -> Unit,
    onClickSave: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            Column {
                IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.Menu, "toggle menu") }
                AppDropDownMenu(
                    expanded = menuExpanded,
                    onClickNew  = onClickNew,
                    onClickOpen = onClickOpen,
                    onClickSave = onClickSave,
                    onDismissRequest = {menuExpanded = false}
                )
            }
         },
        title = { Text("DroidRay") }
    )
}

@Composable
private fun AppDropDownMenu(
    expanded: Boolean,
    onClickNew:  () -> Unit,
    onClickOpen: () -> Unit,
    onClickSave: () -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(text = { Text("New") },  onClick = onClickNew)
        DropdownMenuItem(text = { Text("Open") }, onClick = onClickOpen)
        DropdownMenuItem(text = { Text("Save") }, onClick = onClickSave)
    }
}

@Composable
private fun AppBottomNavigation(currentDestination: Int, onChangeDestination: (Int) -> Unit, showLogBadge: Boolean) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination == 0,
            onClick = { onChangeDestination(0) },
            icon = { Icon(Icons.Default.Create, "editor") },
            label = { Text("Editor") }
        )

        NavigationBarItem(
            selected = currentDestination == 1,
            onClick = { onChangeDestination(1) },
            icon = { Icon(painterResource(id = R.drawable.imagesmode), "render preview") },
            label = { Text("Preview") }
        )

        NavigationBarItem(
            selected = currentDestination == 2,
            onClick = { onChangeDestination(2) },
            icon = {
                BadgedBox(
                    badge = { if (showLogBadge && currentDestination != 2) Badge() }
                ) {
                    Icon(painterResource(id = R.drawable.wysiwyg), "POVRay console log")
                }
           },
            label = { Text("Log") }
        )
    }
}

@Composable
private fun AppFloatingActionButton(rendering: Boolean, onClicked: () -> Unit) {
}

@Preview
@Composable
private fun PreviewApp() {
    DroidRayTheme {
        Surface(Modifier.fillMaxSize()) {
            POVRayApp(onSaveBitmap = {}, onClickOpen = {}, onClickSave = {})
        }
    }
}