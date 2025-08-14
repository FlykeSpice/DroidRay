package com.flykespice.droidray.ui.dialog

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.flykespice.droidray.R
import com.flykespice.droidray.ui.theme.DroidRayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
    onClickLicense: () -> Unit
) {
    val context = LocalContext.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
            ) {
                Text("DroidRay", style = MaterialTheme.typography.headlineLarge)
                Image(
                    context.getDrawable(R.mipmap.ic_launcher)!!.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.large)
                )
                Text("v0.2\n", style = MaterialTheme.typography.bodySmall)

                Text("© 2025 FlykeSpice", style = MaterialTheme.typography.bodySmall)

                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/FlykeSpice/DroidRay".toUri())
                    context.startActivity(intent)
                }) { Text("Github repository") }

                TextButton(onClick = onClickLicense) { Text("View software licenses") }

                Text("POV-Ray™ is a trademark of Persistence of Vision Raytracer Pty. Ltd.", style = MaterialTheme.typography.bodyMedium)

            }
        }
    }
}

@Preview
@Composable
private fun PreviewAboutDialog() {
    DroidRayTheme {
        Surface(Modifier.fillMaxSize()) {
            AboutDialog(onDismissRequest = {}, onClickLicense = {})
        }
    }
}