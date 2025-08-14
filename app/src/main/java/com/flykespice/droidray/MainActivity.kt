package com.flykespice.droidray

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.flykespice.droidray.ui.LicensesScreen
import com.flykespice.droidray.ui.DroidRayApp
import com.flykespice.droidray.ui.IncludesListing
import com.flykespice.droidray.ui.LicenseRead
import com.flykespice.droidray.ui.theme.DroidRayTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

//Navigation destinations
private data object MainScreen
private data object LicensesIndex
private data class  ViewLicense(val path: String)
private data object IncludeList

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
                Surface(Modifier.fillMaxSize()) {
                    val backstack = remember { mutableStateListOf<Any>(MainScreen) }

                    NavDisplay(backStack = backstack) { key ->
                        when (key) {
                            is MainScreen -> NavEntry(key) {
                                DroidRayApp(
                                    onSaveBitmap = { bitmap = it; launcherBitmapExporter.launch("render.png") },
                                    onClickOpen  = { launcherOpener.launch(arrayOf("text/*", "application/*")) },
                                    onClickSave  = { launcherSaver.launch(AppState.lastOpenedName ?: "scene.pov")},
                                    onClickViewLicenses = { backstack.add(LicensesIndex) }
                                )
                            }

                            is LicensesIndex -> NavEntry(key) {
                                LicensesScreen(
                                    onBack = { backstack.removeLastOrNull() },
                                    onClickViewLicense = { backstack.add(ViewLicense(it)) },
                                    onClickViewIncludes = { backstack.add(IncludeList) }
                                )
                            }

                            is ViewLicense -> NavEntry(key) {
                                var text by remember { mutableStateOf("")}

                                LaunchedEffect(true) {
                                    assets.open(key.path, AssetManager.ACCESS_BUFFER).use {
                                        text = it.readBytes().toString(Charsets.UTF_8)
                                    }
                                }

                                LicenseRead(
                                    onClickBack = { backstack.removeLastOrNull() },
                                    title = key.path.substringAfter('/').removeSuffix(".txt"),
                                    body = text
                                )
                            }

                            is IncludeList -> NavEntry(key) {
                                val includes = remember { mutableStateListOf<String>() }

                                LaunchedEffect(true) {
                                    val list = assets.list("include")!!
                                        .filter { it.endsWith(".inc") || it.endsWith(".map") }
                                        .map { it.substringAfter('/') }

                                    includes.addAll(list)
                                }

                                IncludesListing(
                                    includes = includes,
                                    onClickViewInclude = { backstack.add(ViewLicense("include/$it")) },
                                    onClickBack = { backstack.removeLastOrNull() }
                                )
                            }

                            else -> throw IllegalStateException("Unknown destination $key")
                        }
                    }
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