package com.awra.catalogue.ui.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awra.catalogue.ui.util.InteractionFeedback
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PdfViewerScreen(
    pdf: PdfItemModel,
    isPinned: Boolean = false,
    onTogglePin: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = (configuration.screenWidthDp * context.resources.displayMetrics.density).toInt()

    var pageCount by remember { mutableIntStateOf(0) }
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fluid spring-animated pinch-to-zoom & pan state
    var targetScale by remember { mutableFloatStateOf(1f) }
    var targetOffsetX by remember { mutableFloatStateOf(0f) }
    var targetOffsetY by remember { mutableFloatStateOf(0f) }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "zoomScale"
    )

    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "zoomOffsetX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = targetOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "zoomOffsetY"
    )

    // Load PDF Renderer on Dispatchers.IO
    DisposableEffect(pdf.id, pdf.uri) {
        isLoading = true
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null

        try {
            if (pdf.uri != null) {
                pfd = context.contentResolver.openFileDescriptor(pdf.uri, "r")
            } else if (pdf.id.startsWith("/")) {
                val file = File(pdf.id)
                if (file.exists()) {
                    pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                }
            }

            if (pfd != null) {
                renderer = PdfRenderer(pfd)
                pdfRenderer = renderer
                pageCount = renderer.pageCount
            }
        } catch (_: Exception) {
            pageCount = 0
        } finally {
            isLoading = false
        }

        onDispose {
            try {
                renderer?.close()
                pfd?.close()
            } catch (_: Exception) {}
        }
    }

    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    Scaffold(
        containerColor = Color(0xFFF5F5F7), // Neutral paper viewer background
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = pdf.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (pageCount > 0) "$pageCount Pages • ${pdf.subtitle}" else pdf.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            InteractionFeedback.playClick(view)
                            onBackClick()
                        },
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            InteractionFeedback.playClick(view)
                            onTogglePin()
                        }
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "Unpin" else "Pin",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (pdfRenderer != null && pageCount > 0) {
                // Interactive Pinch-to-zoom & Pan Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (targetScale * zoom).coerceIn(1f, 4f)
                                if (newScale <= 1.02f) {
                                    targetScale = 1f
                                    targetOffsetX = 0f
                                    targetOffsetY = 0f
                                } else {
                                    targetScale = newScale
                                    val maxOffsetX = (size.width * (newScale - 1f)) / 2f
                                    val maxOffsetY = (size.height * (newScale - 1f)) / 2f
                                    targetOffsetX = (targetOffsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                    targetOffsetY = (targetOffsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (targetScale > 1.2f) {
                                        targetScale = 1f
                                        targetOffsetX = 0f
                                        targetOffsetY = 0f
                                    } else {
                                        targetScale = 2.2f
                                    }
                                }
                            )
                        }
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                            translationX = animatedOffsetX
                            translationY = animatedOffsetY
                        }
                ) {
                    // Continuous Seamless PDF Document View (No cards/borders)
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            count = pageCount,
                            key = { it }
                        ) { pageIndex ->
                            PdfPageSeamlessItem(
                                renderer = pdfRenderer!!,
                                pageIndex = pageIndex,
                                targetWidthPx = screenWidthPx
                            )
                        }
                    }
                }
            } else {
                // Fallback Mock Document View for Reference / Sample PDFs
                SamplePdfContentView(pdf = pdf)
            }

            // Bottom Page Indicator & Zoom Floating Capsule
            if (pageCount > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .height(48.dp)
                        .wrapContentWidth(),
                    shape = ContinuousRoundedRectangle(24.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Page ${firstVisibleItemIndex + 1} / $pageCount",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.surface
                        )

                        IconButton(
                            onClick = {
                                InteractionFeedback.playClick(view)
                                targetScale = (targetScale - 0.25f).coerceAtLeast(1f)
                                if (targetScale <= 1.02f) {
                                    targetScale = 1f
                                    targetOffsetX = 0f
                                    targetOffsetY = 0f
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                InteractionFeedback.playClick(view)
                                targetScale = (targetScale + 0.25f).coerceAtMost(4f)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (animatedScale > 1.05f) {
                            IconButton(
                                onClick = {
                                    InteractionFeedback.playClick(view)
                                    targetScale = 1f
                                    targetOffsetX = 0f
                                    targetOffsetY = 0f
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Zoom",
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfPageSeamlessItem(
    renderer: PdfRenderer,
    pageIndex: Int,
    targetWidthPx: Int
) {
    var bitmap by remember(renderer, pageIndex, targetWidthPx) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(renderer, pageIndex, targetWidthPx) {
        val pageBitmap = withContext(Dispatchers.IO) {
            try {
                renderer.openPage(pageIndex).use { page ->
                    val aspectRatio = page.height.toFloat() / page.width.toFloat()
                    val targetHeightPx = (targetWidthPx * aspectRatio).toInt().coerceAtLeast(1)
                    val bmp = Bitmap.createBitmap(targetWidthPx, targetHeightPx, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bmp
                }
            } catch (_: Exception) {
                null
            }
        }
        bitmap = pageBitmap
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Page ${pageIndex + 1}",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
private fun SamplePdfContentView(
    pdf: PdfItemModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ContinuousRoundedRectangle(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = pdf.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pdf.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    Text(
                        text = "Course Document Notes & Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Chapter 1: Fundamental Principles and Scope\n" +
                                "• Chapter 2: Key Theorems, Derivations, and Definitions\n" +
                                "• Chapter 3: Worked Examples and Problem Solutions\n" +
                                "• Chapter 4: Formula Sheet and Exam Cheat Code Reference\n\n" +
                                "Use the 'Import' button on the PDF List Screen to load your own custom local PDF files directly into this course viewer.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
