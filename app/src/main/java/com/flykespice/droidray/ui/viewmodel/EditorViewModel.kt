package com.flykespice.droidray.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flykespice.droidray.POVRay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(application: Application) : AndroidViewModel(application) {
    private val _povCode = MutableStateFlow("")
    val povCode get() = _povCode.asStateFlow()

    private val _enabled = MutableStateFlow(false)
    val enabled get() = _enabled.asStateFlow()

    fun updateCode(code: String) {
        _povCode.value = code
    }

    fun submit() {
        viewModelScope.launch {
            _enabled.value = false

            val context = getApplication<Application>().applicationContext
            context.openFileOutput("temp.pov", Context.MODE_PRIVATE).use {
                it.write(_povCode.value.toByteArray())
            }

            val error = POVRay.renderScene("${context.filesDir}/temp.pov", "+L${context.filesDir}/include +W320 +H240 -F")
            if (error != POVRay.vfeNoError) {
                TODO("Handle DroidRay rendering error")
            }

            _enabled.value = true
        }
    }
}