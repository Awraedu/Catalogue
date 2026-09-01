package com.awra.catalogue.ui.utility.tools.entrepreneurship

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun GrowthMetricsContent(onClick: (UtilityItemData) -> Unit) {
    val metrics = listOf(
        UtilityItemData(
            "CAC (Customer Acquisition Cost)",
            "Total cost to acquire one new customer",
            "Formula: (Marketing + Sales Costs) / # of New Customers. Essential for measuring marketing efficiency.",
            listOf("Marketing spend / conversion rate"),
            "If CAC > LTV, the business model is unsustainable."
        ),
        UtilityItemData(
            "LTV (Life Time Value)",
            "Total revenue from one customer over time",
            "The total net profit a business expects to earn from a customer throughout their entire relationship.",
            listOf("Average order value x repeat purchase rate"),
            "Startups aim for LTV:CAC ratio of at least 3:1."
        ),
        UtilityItemData(
            "Churn Rate",
            "Percentage of customers who stop using a service",
            "Calculated as (Customers lost in period) / (Customers at start of period). Critical for subscription businesses.",
            listOf("Monthly churn", "Annual churn"),
            "A high churn rate indicates poor product-market fit."
        ),
        UtilityItemData(
            "Burn Rate",
            "Speed at which a company spends its capital",
            "Monthly negative cash flow. Determines the 'runway' of the startup.",
            listOf("Gross burn vs Net burn"),
            "Used to plan fundraising and manage survival time."
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Advanced Unit Economics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(16.dp)
        )
        UtilityList(metrics, onClick)
    }
}
