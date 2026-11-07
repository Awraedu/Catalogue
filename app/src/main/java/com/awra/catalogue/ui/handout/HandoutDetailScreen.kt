package com.awra.catalogue.ui.handout

import android.text.Layout
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.animation.core.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val startTime = remember(handout.id) { System.currentTimeMillis() }
    var hasRecordedSession by remember(handout.id) { mutableStateOf(false) }

    val currentOnFinish by rememberUpdatedState(onFinish)

    // Automatically record study time even if user exits via system Back gesture!
    DisposableEffect(handout.id) {
        onDispose {
            if (!hasRecordedSession) {
                val timeSpent = System.currentTimeMillis() - startTime
                if (timeSpent >= 2000L) { // Only record if read for at least 2 seconds
                    hasRecordedSession = true
                    currentOnFinish(timeSpent)
                }
            }
        }
    }

    var markdownContent by remember(handout.id) { mutableStateOf<String?>(null) }
    var isCheckingLocal by remember(handout.id) { mutableStateOf(true) }

    val downloadStates by viewModel.downloadStates.collectAsState()
    val downloadState = downloadStates[handout.id]

    var showMenu by remember { mutableStateOf(false) }

    // 🚀 Decrypt & read on Dispatchers.IO so the screen transition animation NEVER drops a frame!
    LaunchedEffect(handout.id) {
        isCheckingLocal = true
        val localContent = withContext(Dispatchers.IO) {
            viewModel.getHandoutContent(context, handout)
        }
        markdownContent = localContent
        isCheckingLocal = false

        if (localContent == null && downloadState !is HandoutDownloadState.Downloading) {
            viewModel.downloadHandout(context, handout)
        }
    }

    LaunchedEffect(downloadState) {
        if (downloadState is HandoutDownloadState.Downloaded) {
            markdownContent = downloadState.content
        }
    }

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

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
                            if (!hasRecordedSession) {
                                val timeSpent = System.currentTimeMillis() - startTime
                                if (timeSpent >= 2000L) {
                                    hasRecordedSession = true
                                    onFinish(timeSpent)
                                }
                            }
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
                            textView.setTextColor(textColor)
                            // 🚀 Guard against re-parsing Markdown when unrelated UI states (like Pin or Menu) change!
                            if (textView.tag != content) {
                                textView.tag = content
                                markwon.setMarkdown(textView, content)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(100.dp))
                }
            } else if (isCheckingLocal) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (downloadState) {
                    is HandoutDownloadState.Downloading -> {
                        val rawProgress = downloadState.progress
                        val animatedProgress by animateFloatAsState(
                            targetValue = rawProgress,
                            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                            label = "download_progress"
                        )
                        val percentage = (animatedProgress * 100).toInt()

                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.96f,
                            targetValue = 1.04f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                shape = ContinuousRoundedRectangle(36.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(120.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size((110 * pulseScale).dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                        )

                                        CircularProgressIndicator(
                                            progress = { animatedProgress },
                                            modifier = Modifier.size(96.dp),
                                            strokeWidth = 6.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudDownload,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp),
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Downloading Study Material",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Decrypting & preparing content for offline study",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Download Progress",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "$percentage%",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        LinearProgressIndicator(
                                            progress = { animatedProgress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(CircleShape),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "AES-256 Encrypted on Device",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is HandoutDownloadState.Error -> {
                        val errorMsg = downloadState.message
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                shape = ContinuousRoundedRectangle(36.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(32.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Download Failed",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = errorMsg,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.downloadHandout(context, handout) },
                                        shape = ContinuousRoundedRectangle(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        ),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Retry Download", fontWeight = FontWeight.Bold)
                                    }
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