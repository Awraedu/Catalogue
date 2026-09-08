package com.awra.catalogue.ui.handout

import android.text.Layout
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.awra.catalogue.data.model.Handout
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandoutDetailScreen(
    handout: Handout,
    onBackClick: () -> Unit,
    onFinish: (Long) -> Unit
) {
    val context = LocalContext.current
    val startTime = remember { System.currentTimeMillis() }

    // Load markdown content from assets
    val markdownContent = remember(handout.content) {
        try {
            context.assets
                .open(handout.content)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            "# Error\nCould not load handout content from ${handout.content}: ${e.message}"
        }
    }

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    // Initialize Markwon
    val markwon = remember(context, textColor) {
        Markwon.builder(context)
            .usePlugin(TablePlugin.create(context))
            .usePlugin(
                JLatexMathPlugin.create(
                    JLatexMathPlugin.builder(36f).apply {
                        inlinesEnabled(true)
                        theme().textColor(textColor)
                    }.build()
                )
            )
            .usePlugin(CoilImagesPlugin.create(context))
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(TaskListPlugin.create(context))
            .build()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Study Material",
                        style = MaterialTheme.typography.titleMedium
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            val timeSpent =
                                System.currentTimeMillis() - startTime

                            onFinish(timeSpent)
                            onBackClick()
                        },
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
        ) {

            AndroidView(
                modifier = Modifier.fillMaxWidth(),

                factory = { ctx ->

                    TextView(ctx).apply {

                        movementMethod =
                            LinkMovementMethod.getInstance()

                        textSize = 18f

                        setLineSpacing(
                            0f,
                            1.4f
                        )

                        includeFontPadding = false

                        setTextColor(textColor)

                        /*
                         * Only allow clicks on task-list checkboxes.
                         *
                         * Normal text remains non-clickable.
                         * Links still work through LinkMovementMethod.
                         */
                        setOnTouchListener { view, event ->

                            if (event.action != MotionEvent.ACTION_UP) {
                                return@setOnTouchListener false
                            }

                            val textView = view as TextView
                            val text = textView.text

                            if (text !is android.text.Spanned) {
                                return@setOnTouchListener false
                            }

                            val x =
                                event.x -
                                        textView.totalPaddingLeft +
                                        textView.scrollX

                            val y =
                                event.y -
                                        textView.totalPaddingTop +
                                        textView.scrollY

                            val layout: Layout =
                                textView.layout
                                    ?: return@setOnTouchListener false

                            val line =
                                layout.getLineForVertical(
                                    y.toInt()
                                )

                            val offset =
                                layout.getOffsetForHorizontal(
                                    line,
                                    x
                                )

                            /*
                             * Find a clickable span at the tapped
                             * position.
                             *
                             * TaskListPlugin uses a clickable span
                             * around the checkbox area.
                             */
                            val spans =
                                text.getSpans(
                                    offset,
                                    offset,
                                    android.text.style.ClickableSpan::class.java
                                )

                            if (spans.isNotEmpty()) {

                                val span = spans.first()

                                /*
                                 * Only execute the span when the tap
                                 * is actually near the beginning of
                                 * the task-list item.
                                 *
                                 * This prevents the entire task text
                                 * from behaving like a checkbox.
                                 */
                                val lineStart =
                                    layout.getLineStart(line)

                                val tapOffset =
                                    offset - lineStart

                                /*
                                 * Checkbox is located at the beginning
                                 * of the task-list line.
                                 */
                                if (tapOffset <= 4) {

                                    span.onClick(textView)

                                    true
                                } else {
                                    false
                                }

                            } else {
                                false
                            }
                        }
                    }
                },

                update = { textView ->
                    markwon.setMarkdown(
                        textView,
                        markdownContent
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(100.dp)
            )
        }
    }
}