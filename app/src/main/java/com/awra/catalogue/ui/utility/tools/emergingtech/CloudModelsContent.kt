package com.awra.catalogue.ui.utility.tools.emergingtech

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData

@Composable
fun CloudModelsContent(onClick: (UtilityItemData) -> Unit) {
    val models = listOf(
        CloudModel(
            "IaaS", 
            "Infrastructure as a Service", 
            "Rent servers, storage, and networking. You manage the OS and apps.",
            listOf("AWS EC2", "Google Compute Engine", "Microsoft Azure VMs"),
            MaterialTheme.colorScheme.primaryContainer
        ),
        CloudModel(
            "PaaS", 
            "Platform as a Service", 
            "Platform for developers to build/deploy apps. Cloud provider manages OS and middleware.",
            listOf("Heroku", "Google App Engine", "Azure App Service"),
            MaterialTheme.colorScheme.secondaryContainer
        ),
        CloudModel(
            "SaaS", 
            "Software as a Service", 
            "Finished software delivered over the web. User only manages the data.",
            listOf("Google Workspace", "Salesforce", "Dropbox", "Slack"),
            MaterialTheme.colorScheme.tertiaryContainer
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Cloud Service Models",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Who manages what?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        models.forEach { model ->
            Surface(
                onClick = {
                    onClick(
                        UtilityItemData(
                            title = model.title,
                            shortDesc = model.fullName,
                            detailedExplanation = model.desc,
                            examples = model.examples,
                            realWorldUse = "Used to reduce IT costs and scale applications rapidly."
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = model.color,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Cloud, null, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(model.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(model.fullName, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

private data class CloudModel(
    val title: String, 
    val fullName: String, 
    val desc: String, 
    val examples: List<String>, 
    val color: androidx.compose.ui.graphics.Color
)
