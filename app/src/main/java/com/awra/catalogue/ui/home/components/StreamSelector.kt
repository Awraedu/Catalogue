package com.awra.catalogue.ui.home.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.awra.catalogue.data.model.Stream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamSelector(
    selectedStream: Stream,
    onStreamSelected: (Stream) -> Unit,
    modifier: Modifier = Modifier
) {
    val streams = Stream.entries
    val view = LocalView.current
    
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        streams.forEachIndexed { index, stream ->
            SegmentedButton(
                selected = stream == selectedStream,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onStreamSelected(stream)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = streams.size),
                label = { 
                    Text(
                        text = stream.name,
                        style = MaterialTheme.typography.labelLarge
                    ) 
                }
            )
        }
    }
}
