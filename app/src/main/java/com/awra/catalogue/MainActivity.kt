package com.awra.catalogue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awra.catalogue.data.database.AppDatabase
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.Subject
import com.awra.catalogue.data.repository.CatalogueRepository
import com.awra.catalogue.ui.handout.HandoutListScreen
import com.awra.catalogue.ui.handout.HandoutViewModel
import com.awra.catalogue.ui.home.HomeScreen
import com.awra.catalogue.ui.home.HomeViewModel
import com.awra.catalogue.ui.subject.SubjectDetailScreen
import com.awra.catalogue.ui.theme.YourAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = CatalogueRepository(database.catalogueDao())
        
        val homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        })[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            YourAppTheme {
                var currentSubject by remember { mutableStateOf<Subject?>(null) }
                var currentMaterialType by remember { mutableStateOf<MaterialType?>(null) }
                
                when {
                    currentSubject == null -> {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onSubjectClick = { currentSubject = it }
                        )
                    }
                    currentMaterialType == null -> {
                        SubjectDetailScreen(
                            subject = currentSubject!!,
                            onBackClick = { currentSubject = null },
                            onCategoryClick = { currentMaterialType = it },
                            onContinueClick = { /* Handle resume */ }
                        )
                    }
                    currentMaterialType == MaterialType.HANDOUT -> {
                        val handoutViewModel = remember(currentSubject!!.id) {
                            HandoutViewModel(repository, currentSubject!!.id)
                        }
                        HandoutListScreen(
                            subject = currentSubject!!,
                            viewModel = handoutViewModel,
                            onBackClick = { currentMaterialType = null },
                            onHandoutClick = { /* Handle opening handout */ }
                        )
                    }
                    else -> {
                        Button(onClick = { currentMaterialType = null }) {
                            Text("Feature coming soon. Go Back")
                        }
                    }
                }
            }
        }
    }
}
