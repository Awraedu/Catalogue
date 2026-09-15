package com.awra.catalogue.ui.handout

import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HandoutDetailScreen(
    handout: Handout,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    onBackClick: () -> Unit,
    onFinish: (Long) -> Unit
) {
    val context = LocalContext.current
    val startTime = remember { System.currentTimeMillis() }

    var isSearchActive by remember { mutableStateOf(false) }
    val searchTextFieldState = rememberTextFieldState("")
    val searchBarState = rememberSearchBarState()

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
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f).toArgb()

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
            Box {
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
                                val timeSpent = System.currentTimeMillis() - startTime
                                onFinish(timeSpent)
                                onBackClick()
                            },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onTogglePin) {
                            Icon(
                                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (isPinned) "Unpin" else "Pin",
                                tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val inputField = remember(searchTextFieldState, searchBarState) {
                        @Composable {
                            SearchBarDefaults.InputField(
                                textFieldState = searchTextFieldState,
                                searchBarState = searchBarState,
                                onSearch = { isSearchActive = false },
                                placeholder = { Text("Find in handout...") },
                                leadingIcon = {
                                    IconButton(onClick = { isSearchActive = false }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close search")
                                    }
                                },
                                trailingIcon = {
                                    if (searchTextFieldState.text.isNotEmpty()) {
                                        IconButton(onClick = { searchTextFieldState.clearText() }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                }
                            )
                        }
                    }

                    SearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    TextView(ctx).apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        textSize = 18f
                        setLineSpacing(0f, 1.4f)
                        includeFontPadding = false
                        setTextColor(textColor)

                        setOnTouchListener { view, event ->
                            if (event.action != MotionEvent.ACTION_UP) {
                                return@setOnTouchListener false
                            }
                            val textView = view as TextView
                            val text = textView.text
                            if (text !is android.text.Spanned) {
                                return@setOnTouchListener false
                            }
                            val x = event.x - textView.totalPaddingLeft + textView.scrollX
                            val y = event.y - textView.totalPaddingTop + textView.scrollY
                            val layout: Layout = textView.layout ?: return@setOnTouchListener false
                            val line = layout.getLineForVertical(y.toInt())
                            val offset = layout.getOffsetForHorizontal(line, x)
                            val spans = text.getSpans(offset, offset, android.text.style.ClickableSpan::class.java)
                            if (spans.isNotEmpty()) {
                                val span = spans.first()
                                val lineStart = layout.getLineStart(line)
                                val tapOffset = offset - lineStart
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
                    val query = searchTextFieldState.text.toString()
                    markwon.setMarkdown(textView, markdownContent)
                    
                    if (query.isNotEmpty() && query.length >= 2) {
                        val fullText = textView.text
                        val spannable = SpannableStringBuilder(fullText)
                        val lowerFullText = fullText.toString().lowercase()
                        val lowerQuery = query.lowercase()
                        
                        var start = lowerFullText.indexOf(lowerQuery)
                        while (start >= 0) {
                            spannable.setSpan(
                                BackgroundColorSpan(highlightColor),
                                start,
                                start + query.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            start = lowerFullText.indexOf(lowerQuery, start + query.length)
                        }
                        textView.text = spannable
                    }
                }
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
