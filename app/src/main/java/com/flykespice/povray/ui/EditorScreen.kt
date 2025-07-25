package com.flykespice.povray.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    textField: TextFieldValue,
    scrollState: ScrollState,
    onValueChange: (TextFieldValue) -> Unit,
    styleText: (String) -> AnnotatedString,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        //color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .imePadding()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val constraints = constraints

            Row(modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
            ) {
                var visualTransformation by remember { mutableStateOf(VisualTransformation.None, neverEqualPolicy()) }
                var visualSpanStyle: List<AnnotatedString.Range<SpanStyle>> = remember { AnnotatedString("").spanStyles }
                var oldText = remember { textField.text }

                LaunchedEffect(textField.text) {
                    delay(500) //Cooldown, so that it only gets update after user stops typing
                    visualSpanStyle = styleText(textField.text).spanStyles
                    oldText = textField.text
                    //reset with same value to force BasicTextField to recompose with new styling
                    visualTransformation = VisualTransformation {
                        if (it.text != oldText) {
                            visualSpanStyle = adjustSpanStyle(visualSpanStyle, oldText, it.text, textField.selection.start)
                            oldText = it.text
                        }

                        TransformedText(AnnotatedString(it.text, visualSpanStyle), OffsetMapping.Identity)
                    }
                }

                val lines = remember(textField.text) {
                    textField.text.lines().indices.joinToString("\n") { (it + 1).toString().padStart(3) }
                }

                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = lines
                )

                Spacer(Modifier.width(3.dp))

                val fontHeight = with(LocalDensity.current) { MaterialTheme.typography.titleSmall.lineHeight.toDp().roundToPx() }

                LaunchedEffect(constraints) {
                    delay(300)

                    if(textField.selection.start >= textField.text.length)
                        return@LaunchedEffect

                    val cursorLine = textField.text.subSequence(0, textField.selection.start+1).filter { it == '\n' }.length+1

                    val actualPosition = ((cursorLine * fontHeight) - scrollState.value)
                    if(actualPosition >= (constraints.maxHeight - (fontHeight*2))) {
                        //TODO: Scroll until cursor position touches the bottom of the IME padding
                        launch { scrollState.scrollTo(((cursorLine+2)*fontHeight) - constraints.maxHeight)}
                    }
                }

                val horizontalScrollState = rememberScrollState()

                val textStyle = MaterialTheme.typography.titleSmall
                val mergedStyle = textStyle.merge(TextStyle(color = MaterialTheme.colorScheme.onSurface))

                BasicTextField(
                    value = textField,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState),
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Ascii
                    ),
                    textStyle = mergedStyle,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    visualTransformation = visualTransformation
                )
            }
        }
    }
}

//FIXME: This is too slow when there are many text (and styles)
//This code assumes spanstyles is sorted.
private fun adjustSpanStyle(
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    oldText: String,
    newText: String,
    cursorPosition: Int
): List<AnnotatedString.Range<SpanStyle>> {
    if (oldText.isEmpty() || newText.isEmpty())
        return spanStyles

    val displacement = newText.length - oldText.length
    val first = spanStyles.indexOfFirst { it.end >= cursorPosition }

    //Log.d("adjustSpanStyle", "cursorPosition = $cursorPosition")

    if (first == -1)
        return spanStyles

    //val newSpanStyle = ArrayList<AnnotatedString.Range<SpanStyle>>(spanStyles.slice(0 .. first))
    val newSpanStyle = ArrayList<AnnotatedString.Range<SpanStyle>>(spanStyles.size)

    for(i in 0 until spanStyles.size) {
        val style = spanStyles[i]
        var start = style.start
        var end = style.end

        if (start >= cursorPosition)
            start += displacement

        if (end >= cursorPosition)
            end += displacement
        if (start < end)
            newSpanStyle.add(style.copy(start = start, end = end))
    }

    return newSpanStyle
}

