package com.awra.catalogue.ui.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeFAB(
    onTermsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val inverseSurface = MaterialTheme.colorScheme.inverseSurface
    val inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface

    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = { isChecked: Boolean -> expanded = isChecked },
                containerColor = { _: Float -> inverseSurface }
            ) {
                Icon(
                    imageVector = if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Home,
                    contentDescription = "Home Menu",
                    tint = inverseOnSurface
                )
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = { expanded = false },
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = inverseOnSurface) },
            text = { Text("Profile", color = inverseOnSurface) },
            containerColor = inverseSurface
        )
        FloatingActionButtonMenuItem(
            onClick = { expanded = false },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null, tint = inverseOnSurface) },
            text = { Text("Analytics", color = inverseOnSurface) },
            containerColor = inverseSurface
        )
        FloatingActionButtonMenuItem(
            onClick = { 
                expanded = false
                onTermsClick()
            },
            icon = { Icon(Icons.Default.Book, contentDescription = null, tint = inverseOnSurface) },
            text = { Text("Terms", color = inverseOnSurface) },
            containerColor = inverseSurface
        )
    }
}
