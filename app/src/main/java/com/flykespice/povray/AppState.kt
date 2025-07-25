package com.flykespice.povray

/**
 * This stores the application state for the duration of the process in a simple way
 */
object AppState {
    var povCode: String = ""
    var width: Int = 320 //Saved width option
    var height: Int = 240 //Saved height option

    var lastOpenedName: String? = null
}