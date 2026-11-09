package com.awra.catalogue.ui.pdf

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.awra.catalogue.R
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.util.PreferenceManager
import com.awra.catalogue.ui.home.components.AmbientMeshBackground
import com.awra.catalogue.ui.util.InteractionFeedback
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlinx.coroutines.launch
import java.io.File

data class PdfItemModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val uri: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PdfListScreen(
    subject: Subject,
    onBackClick: () -> Unit,
    onPdfClick: (PdfItemModel) -> Unit
) {
    val context = LocalContext.current
    val preferenceManager = remember(context) { PreferenceManager(context) }
    var customPdfs by remember(subject.id) {
        mutableStateOf(preferenceManager.getCustomPdfs(subject.id))
    }
    var searchQuery by remember { mutableStateOf("") }
    val view = LocalView.current

    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImportTitle by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val realName = getRealFileNameFromUri(context, it)
            pendingImportUri = it
            pendingImportTitle = realName
            showImportDialog = true
        }
    }

    val defaultPdfs = remember(subject.name) {
        listOf(
            PdfItemModel(
                id = "def_1",
                title = "${subject.name} Complete Course Syllabus & Summary",
                subtitle = "Official Course Reference PDF"
            ),
            PdfItemModel(
                id = "def_2",
                title = "${subject.name} Past Exam Questions & Solutions",
                subtitle = "Practice Questions Document"
            ),
            PdfItemModel(
                id = "def_3",
                title = "${subject.name} Formula & Key Concepts Sheet",
                subtitle = "Reference Cheat Sheet"
            )
        )
    }

    val allPdfs = remember(defaultPdfs, customPdfs) { defaultPdfs + customPdfs }

    val filteredPdfs = remember(allPdfs, searchQuery) {
        if (searchQuery.isBlank()) allPdfs
        else allPdfs.filter { it.title.contains(searchQuery, ignoreCase = true) || it.subtitle.contains(searchQuery, ignoreCase = true) }
    }

    val accentColor = MaterialTheme.colorScheme.primary

    val textFieldState = rememberTextFieldState(searchQuery)
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()

    val isSearchActive by remember {
        derivedStateOf {
            searchBarState.currentValue == SearchBarValue.Expanded ||
                    searchBarState.targetValue == SearchBarValue.Expanded
        }
    }

    LaunchedEffect(textFieldState.text) {
        searchQuery = textFieldState.text.toString()
    }

    val inputField = remember(textFieldState, searchBarState) {
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(
                        "Search PDFs or documents...",
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                    )
                },
                leadingIcon = {
                    IconButton(onClick = {
                        InteractionFeedback.playClick(view)
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            scope.launch { searchBarState.animateToCollapsed() }
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = {
                            InteractionFeedback.playClick(view)
                            textFieldState.clearText()
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    } else if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                },
                colors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.surface,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }

    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            AmbientMeshBackground(
                modifier = Modifier.fillMaxSize(),
                primaryColor = accentColor
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { isTraversalGroup = true }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(key = "top_spacer") {
                        Spacer(modifier = Modifier.statusBarsPadding().height(72.dp))
                    }

                    item(key = "subject_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "PDF Documents",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            Button(
                                onClick = {
                                    InteractionFeedback.playClick(view)
                                    pdfPickerLauncher.launch("application/pdf")
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Import", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item(key = "pdf_section_title") {
                        Text(
                            text = "Course PDFs & Documents",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                        )
                    }

                    itemsIndexed(
                        items = filteredPdfs,
                        key = { _, item -> item.id }
                    ) { index, pdf ->
                        PdfListItem(
                            pdf = pdf,
                            index = index,
                            count = filteredPdfs.size,
                            onClick = {
                                InteractionFeedback.playClick(view)
                                onPdfClick(pdf)
                            }
                        )

                        if (index < filteredPdfs.size - 1) {
                            Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
                        }
                    }

                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Top Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(135.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(40f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = 1.0 - smoothstep(size.y * 0.1, size.y, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        // Bottom Bar Progressive Blur
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(40f.dp.toPx())
                        runtimeShaderEffect(
                            "ProgressiveBlur",
                            """
                            uniform shader content;
                            uniform float2 size;
                            half4 main(float2 coord) {
                                float alpha = smoothstep(0.0, size.y * 0.9, coord.y);
                                return content.eval(coord) * alpha;
                            }
                            """,
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                        }
                    }
                )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                state = searchBarState,
                inputField = inputField,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { traversalIndex = 0f },
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = CircleShape
            )

            if (isSearchActive) {
                ExpandedFullScreenSearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    val query = textFieldState.text.toString().trim()
                    if (query.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Type to search PDFs",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                        }
                    } else if (filteredPdfs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matches found for \"$query\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            itemsIndexed(
                                items = filteredPdfs,
                                key = { _, item -> "s_${item.id}" }
                            ) { index, pdf ->
                                PdfListItem(
                                    pdf = pdf,
                                    index = index,
                                    count = filteredPdfs.size,
                                    onClick = {
                                        InteractionFeedback.playClick(view)
                                        scope.launch { searchBarState.animateToCollapsed() }
                                        onPdfClick(pdf)
                                    }
                                )

                                if (index < filteredPdfs.size - 1) {
                                    Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showImportDialog && pendingImportUri != null) {
            AlertDialog(
                onDismissRequest = {
                    showImportDialog = false
                    pendingImportUri = null
                },
                title = { Text("Import PDF Document", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Customize the document title before adding it to ${subject.name}:")
                        OutlinedTextField(
                            value = pendingImportTitle,
                            onValueChange = { pendingImportTitle = it },
                            label = { Text("PDF Title") },
                            singleLine = true,
                            shape = ContinuousRoundedRectangle(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val finalTitle = pendingImportTitle.trim().ifEmpty { "Imported Document" }
                            val savedPdf = copyPdfToAppStorage(context, pendingImportUri!!, subject.id, finalTitle)
                            preferenceManager.saveCustomPdf(subject.id, savedPdf)
                            customPdfs = preferenceManager.getCustomPdfs(subject.id)
                            InteractionFeedback.playConfirm(view)
                            showImportDialog = false
                            pendingImportUri = null
                        },
                        shape = ContinuousRoundedRectangle(16.dp)
                    ) {
                        Text("Import", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showImportDialog = false
                            pendingImportUri = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                shape = ContinuousRoundedRectangle(28.dp)
            )
        }
    }
}

private fun getRealFileNameFromUri(context: Context, uri: Uri): String {
    var fileName: String? = null
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    if (fileName.isNullOrEmpty()) {
        fileName = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':')
    }

    return fileName
        ?.removeSuffix(".pdf")
        ?.removeSuffix(".PDF")
        ?.trim()
        ?.ifEmpty { "Imported Document" }
        ?: "Imported Document"
}

private fun copyPdfToAppStorage(context: Context, uri: Uri, subjectId: Long, customTitle: String): PdfItemModel {
    return try {
        val pdfsDir = File(context.filesDir, "imported_pdfs/$subjectId").apply { mkdirs() }
        val safeTitle = customTitle.take(30).replace(Regex("[^a-zA-Z0-9_]"), "_")
        val destFile = File(pdfsDir, "pdf_${System.currentTimeMillis()}_$safeTitle.pdf")

        context.contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        PdfItemModel(
            id = destFile.absolutePath,
            title = customTitle,
            subtitle = "Custom Saved PDF",
            uri = Uri.fromFile(destFile)
        )
    } catch (_: Exception) {
        PdfItemModel(
            id = uri.toString(),
            title = customTitle,
            subtitle = "Custom Saved PDF",
            uri = uri
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PdfListItem(
    pdf: PdfItemModel,
    index: Int,
    count: Int,
    onClick: () -> Unit
) {
    val outerRadius = 38.dp
    val innerRadius = 6.dp
    val itemShape = remember(index, count) {
        when {
            count == 1 -> ContinuousRoundedRectangle(outerRadius)
            index == 0 -> RoundedCornerShape(topStart = outerRadius, topEnd = outerRadius, bottomStart = innerRadius, bottomEnd = innerRadius)
            index == count - 1 -> RoundedCornerShape(bottomStart = outerRadius, bottomEnd = outerRadius, topStart = innerRadius, topEnd = innerRadius)
            else -> RoundedCornerShape(innerRadius)
        }
    }

    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(shape = itemShape),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.pdfs),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        },
        supportingContent = {
            Text(pdf.subtitle)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.90f)
        )
    ) {
        Text(
            text = pdf.title,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
