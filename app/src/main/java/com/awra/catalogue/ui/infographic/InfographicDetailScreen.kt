package com.awra.catalogue.ui.infographic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.awra.catalogue.R
import com.awra.catalogue.data.model.Infographic
import com.awra.catalogue.ui.theme.YourAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfographicDetailScreen(
    infographic: Infographic,
    isPinned: Boolean,
    onTogglePin: () -> Unit,
    onBackClick: () -> Unit,
    onFinish: (Long) -> Unit
) {
    val startTime = remember { System.currentTimeMillis() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Resolve resource ID from name (e.g., "bio.png" -> R.drawable.bio)
    val imageResId = remember(infographic.imageUrl) {
        val resourceName = infographic.imageUrl.substringBeforeLast(".")
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Infographic", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(
                        onClick = { 
                            val timeSpent = System.currentTimeMillis() - startTime
                            onFinish(timeSpent)
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
                    IconButton(onClick = onTogglePin) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) "Unpin" else "Pin",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                val painter = if (imageResId != 0) {
                    painterResource(id = imageResId)
                } else {
                    painterResource(id = R.drawable.bio)
                }

                Image(
                    painter = painter,
                    contentDescription = "Infographic: ${infographic.imageUrl}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
            }
            
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfographicDetailPreview() {
    YourAppTheme {
        InfographicDetailScreen(
            infographic = Infographic(1, 1, "bio.png"),
            isPinned = false,
            onTogglePin = {},
            onBackClick = {},
            onFinish = {}
        )
    }
}
