package com.flykespice.povray.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flykespice.povray.ui.theme.POVRayTheme

@Composable
fun RenderOptionsDialog(
    width: Int,
    height: Int,
    antialias: Boolean,
    onConfirm: (width: Int, height: Int, antialias: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val widthTextState = rememberTextFieldState(width.toString())
    val heightTextState = rememberTextFieldState(height.toString())
    var antialias by remember { mutableStateOf(antialias) }

    AlertDialog(
        title = { Text("Render Options") },
        text = {
            Column {
                Row {
                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("W", style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            state = widthTextState,
                            textStyle = MaterialTheme.typography.titleLarge,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            contentPadding = OutlinedTextFieldDefaults.contentPadding(
                                top = 0.2.dp,
                                bottom = 0.2.dp
                            )
                        )
                    }

                    Text("X",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 42.dp)
                    )

                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("H", style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            state = heightTextState,
                            textStyle = MaterialTheme.typography.titleLarge,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            contentPadding = OutlinedTextFieldDefaults.contentPadding(
                                top = 0.2.dp,
                                bottom = 0.2.dp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Antialias:", style = MaterialTheme.typography.titleLarge)
                    Checkbox(checked = antialias, onCheckedChange = { antialias = it })
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = { onConfirm(widthTextState.text.toString().toInt(), heightTextState.text.toString().toInt(), antialias) },
                enabled = widthTextState.text.isNotBlank() && heightTextState.text.isNotBlank()
            ) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel", color = Color.Red) } }
    )
}

@Preview
@Composable
private fun PreviewRenderOptionsDialog() {
    POVRayTheme {
        Surface {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                var dialogOpened by remember { mutableStateOf(true) }
                if (dialogOpened) {
                    RenderOptionsDialog(
                        width = 320,
                        height = 240,
                        antialias = false,
                        onConfirm = { _,_,_ -> },
                        onCancel = {}
                    )
                }

                Button(onClick = { dialogOpened = true }) {
                    Text("Popup Render Resolution Dialog")
                }
            }
        }
    }
}
