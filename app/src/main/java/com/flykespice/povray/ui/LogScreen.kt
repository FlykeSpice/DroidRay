package com.flykespice.povray.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.flykespice.povray.POVRay
import com.flykespice.povray.ui.theme.POVRayTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(log: List<POVRay.Message>) {
    val scrollState = rememberScrollState(-1)
    val horizontalScrollState = rememberScrollState()
    var fold by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize()) {
/*        Row {
            Text("Filter:")

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = it}) {
                for (i in listOf(""))
            }
        }*/


        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Fold:", style = MaterialTheme.typography.titleLarge)
            Checkbox(checked = fold, onCheckedChange = {fold = it})
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            val modifier = remember (fold) {
                if (fold)
                    Modifier
                        .padding(5.dp)
                else
                    Modifier
                        .horizontalScroll(horizontalScrollState)
                        .padding(5.dp)
            }

            LazyColumn(modifier) {
                items(log) { message ->
                    Text(message.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (message.type) {
                            POVRay.Message.TYPE_ERROR -> Color.Red
                            POVRay.Message.TYPE_WARNING -> Color.Yellow
                            else -> Color.Unspecified
                        }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewLogScreen() {
    val sampleLog = listOf(
        POVRay.Message(POVRay.Message.TYPE_GENERIC_STATUS, "Starting Render"),
        POVRay.Message(POVRay.Message.TYPE_INFO, "Rendering Done")
    )

    POVRayTheme {
        Surface (Modifier.fillMaxSize()) {
            LogScreen(log = sampleLog)
        }
    }
}