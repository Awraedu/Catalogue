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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.awra.catalogue.data.model.Handout
import com.kyant.capsule.ContinuousRoundedRectangle
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HandoutDetailScreen(
    handout: Handout,
    viewModel: HandoutViewModel,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    onBackClick: () -> Unit,
    onFinish: (Long) -> Unit
) {
    val context = LocalContext.current
    val startTime = remember { System.currentTimeMillis() }

    var markdownContent by remember(handout.id) {
        mutableStateOf(viewModel.getHandoutContent(context, handout))
    }

    val downloadStates by viewModel.downloadStates.collectAsState()
    val downloadState = downloadStates[handout.id]

    var showMenu by remember { mutableStateOf(false) }

    // Auto-trigger download if not present locally
    LaunchedEffect(handout.id) {
        if (markdownContent == null && downloadState !is HandoutDownloadState.Downloading) {
            viewModel.downloadHandout(context, handout)
        }
    }

    LaunchedEffect(downloadState) {
        if (downloadState is HandoutDownloadState.Downloaded) {
            markdownContent = (downloadState as HandoutDownloadState.Downloaded).content
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        SplitButtonLayout(
                            leadingButton = {
                                SplitButtonDefaults.LeadingButton(
                                    onClick = onTogglePin,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = if (isPinned)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = if (isPinned)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                            contentDescription = if (isPinned) "Unpin" else "Pin",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isPinned) "Pinned" else "Pin",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            trailingButton = {
                                SplitButtonDefaults.TrailingButton(
                                    onClick = { showMenu = !showMenu },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = if (isPinned)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerHighest,
                                        contentColor = if (isPinned)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More options",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Re-download Handout") },
                                leadingIcon = { Icon(Icons.Default.Refresh, null) },
                                onClick = {
                                    showMenu = false
                                    markdownContent = null
                                    viewModel.downloadHandout(context, handout)
                                }
                            )
                            if (markdownContent != null) {
                                DropdownMenuItem(
                                    text = { Text("Delete Local File") },
                                    leadingIcon = { Icon(Icons.Default.Delete, null) },
                                    onClick = {
                                        showMenu = false
                                        viewModel.deleteHandout(context, handout)
                                        markdownContent = null
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val content = markdownContent
            if (content != null) {
                Column(
                    modifier = Modifier
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
                            markwon.setMarkdown(textView, content)
                        }
                    )
                    Spacer(modifier = Modifier.height(100.dp))
                }
            } else {
                when (downloadState) {
                    is HandoutDownloadState.Downloading -> {
                        val progress = (downloadState as HandoutDownloadState.Downloading).progress
                        Card(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            shape = ContinuousRoundedRectangle(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "Downloading Handout",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Fetching study materials...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "AES-256 Encrypted on Device",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    is HandoutDownloadState.Error -> {
                        val errorMsg = (downloadState as HandoutDownloadState.Error).message
                        Card(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            shape = ContinuousRoundedRectangle(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Download Failed",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = errorMsg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { viewModel.downloadHandout(context, handout) },
                                    shape = ContinuousRoundedRectangle(16.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Retry Download")
                                }
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
