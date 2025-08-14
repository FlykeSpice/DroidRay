package com.flykespice.droidray.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.flykespice.droidray.ui.theme.DroidRayTheme
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClickBack: () -> Unit, title: String) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        title = { Text(title) }
    )
}

@Composable
fun LicensesScreen(
    onBack: () -> Unit,
    onClickViewLicense: (String) -> Unit,
    onClickViewIncludes: () -> Unit
) {
    val context = LocalContext.current

    Column {
        TopBar(onClickBack = onBack, title = "Licenses")

        ListItem(
            modifier = Modifier.clickable {
                onClickViewLicense("license/AGPL-3.0.txt")
            },
            headlineContent = { Text("AGPL-3.0") },
            trailingContent = { Text("POV-Ray, app") }
        )

        ListItem(
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, "https://creativecommons.org/licenses/by-sa/3.0/".toUri())
                context.startActivity(intent)
            },
            headlineContent = { Text("CC BY-SA 3.0 (link)") },
            trailingContent = { Text("POV-Ray standard includes") }
        )

        ListItem(
            modifier = Modifier.clickable { onClickViewIncludes() },
            headlineContent = { Text("POV-Ray Standard Includes") },
        )
    }
}

@Composable
fun IncludesListing(
    includes: List<String>,
    onClickViewInclude: (String) -> Unit,
    onClickBack: () -> Unit
) {
    Column {
        TopBar(onClickBack = onClickBack, title = "POV-Ray Standard Includes")

        val scrollState = rememberScrollState()
        Column(Modifier.verticalScroll(scrollState)) {
            includes.forEach { include ->
                ListItem(
                    modifier = Modifier.clickable { onClickViewInclude(include) },
                    headlineContent = { Text(include) },
                    //trailingContent = { Text("POV-Ray standard includes") }
                )
            }
        }
    }
}

@Composable
fun LicenseRead(onClickBack: () -> Unit, title: String, body: String) {
    Column {
        TopBar(onClickBack = onClickBack, title = title)

        val verticalScrollState   = rememberScrollState()
        val horizontalScrollState = rememberScrollState()
        Text(
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState),
            text = body,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun PreviewLicense() {
    DroidRayTheme {
        Surface(Modifier.fillMaxSize()) {
            LicensesScreen(onBack = {}, onClickViewLicense = {}, onClickViewIncludes = {})
        }
    }
}

@Preview
@Composable
private fun PreviewIncludesListing() {
    DroidRayTheme {
        Surface(Modifier.fillMaxSize()) {
            IncludesListing(
                includes = listOf("colors.inc", "makegrass.inc", "finish.inc"),
                onClickViewInclude = {},
                onClickBack = {}
            )
        }
    }
}