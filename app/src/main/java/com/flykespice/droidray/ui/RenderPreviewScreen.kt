package com.flykespice.droidray.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.flykespice.droidray.R
import com.flykespice.droidray.ui.theme.DroidRayTheme

@Composable
fun RenderPreviewScreen(
    previewBitmap: ImageBitmap,
    isRendering: Boolean, //Povray status flag
    onClickSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = previewBitmap,
            modifier = Modifier.size(300.dp),
            contentDescription = "render preview"
        )

        Spacer(Modifier.height(10.dp))

        if (!isRendering) {
            Button(onClick = onClickSave) {
                Text("Save Image")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewRenderPreviewScreen() {
    DroidRayTheme {
        Surface(Modifier.fillMaxSize()) {
            val sampleBitmap = LocalContext.current.getDrawable(R.drawable.ic_launcher_background)!!
                .toBitmap()
                .asImageBitmap()

            RenderPreviewScreen(previewBitmap = sampleBitmap, isRendering = false, onClickSave = {})
        }
    }
}