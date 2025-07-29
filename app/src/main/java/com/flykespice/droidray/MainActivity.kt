package com.flykespice.droidray

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import com.flykespice.droidray.ui.POVRayApp
import com.flykespice.droidray.ui.theme.DroidRayTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var bitmap: Bitmap
    private val launcherBitmapExporter = activityResultRegistry.register("bitmap exporter", ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if(uri != null) {
            val stream = contentResolver.openOutputStream(uri)
            stream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    private val launcherOpener = activityResultRegistry.register("opener", ActivityResultContracts.OpenDocument()) { uri ->
        if(uri != null) {
            val stream = contentResolver.openInputStream(uri)
            stream?.use {
                AppState.povCode = it.readBytes().toString(Charsets.UTF_8)
                AppState.lastOpenedName = File(uri.path).name
            }
        }
    }

    private val launcherSaver = activityResultRegistry.register("saver", ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if(uri != null) {
            val stream = contentResolver.openOutputStream(uri)
            stream?.use {
                it.write(AppState.povCode.toByteArray())
                AppState.lastOpenedName = File(uri.path).name
            }
        }
    }

    private lateinit var directoryUri: Uri
    private val launcherGrantDirectory = activityResultRegistry.register("grantDirAcess", ActivityResultContracts.OpenDocumentTree()) { uri ->
        directoryUri = uri!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val includeDir = File("$filesDir", "include")
        if (!includeDir.exists()) {
            includeDir.mkdirs()
            copyAssetsToInternalStorage(includeDir)
        }/* else {
            Log.d("DroidRay", "directory include contents =")
            for (file in includeDir.list().toList()) {
                Log.d("DroidRay", "$file")
            }
        }*/

        try {
            openFileInput("savedCode").use {
                AppState.povCode = it.readBytes().toString(charset = Charsets.UTF_8)
            }
        } catch (_: FileNotFoundException) {} //Do nothing

        try {
            openFileInput("lastFilename").use {
                AppState.lastOpenedName = it.readBytes().toString(charset = Charsets.UTF_8)
            }
        } catch (_: FileNotFoundException) {} //Do nothing

        enableEdgeToEdge()
        setContent {
            DroidRayTheme {
                Surface {
                    POVRayApp(
                        onSaveBitmap = { bitmap = it; launcherBitmapExporter.launch("render.png") },
                        onClickOpen = { launcherOpener.launch(arrayOf("application/octet-stream")) },
                        onClickSave = { launcherSaver.launch(AppState.lastOpenedName ?: "scene.pov")}
                    )
                }
            }
        }
    }

    private fun copyAssetsToInternalStorage(includeDir: File) {
        val assetManager = assets
        val files = assetManager.list("include") ?: throw IllegalStateException("assets folder is empty") // List all assets in the root directory

        files.forEach { filename ->
            val inputStream = assetManager.open("include/$filename")

            inputStream.use {
                val file = File(includeDir, filename)
                FileOutputStream(file).use {
                    it.write(inputStream.readBytes())
                }
            }
        }
    }

    override fun onPause() {
        openFileOutput("savedCode", MODE_PRIVATE).use {
            it.write(AppState.povCode.toByteArray())
        }

        if (AppState.lastOpenedName != null) {
            openFileOutput("lastFilename", MODE_PRIVATE).use {
                it.write(AppState.lastOpenedName!!.toByteArray())
            }
        }
        super.onPause()
    }
}