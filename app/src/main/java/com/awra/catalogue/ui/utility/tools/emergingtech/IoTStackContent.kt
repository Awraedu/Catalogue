package com.awra.catalogue.ui.utility.tools.emergingtech

import androidx.compose.runtime.Composable
import com.awra.catalogue.ui.utility.UtilityItemData
import com.awra.catalogue.ui.utility.UtilityList

@Composable
fun IoTStackContent(onClick: (UtilityItemData) -> Unit) {
    val layers = listOf(
        UtilityItemData(
            "Perception Layer",
            "Physical sensors and actuators",
            "The physical layer that interacts with the real world. It includes sensors that gather data and actuators that perform actions.",
            listOf("GPS", "RFID", "Temperature Sensors", "Cameras"),
            "Foundation of smart devices and industrial sensing."
        ),
        UtilityItemData(
            "Network Layer",
            "Transmission and communication",
            "Responsible for transmitting the data collected at the perception layer to the processing system.",
            listOf("WiFi", "5G/6G", "Bluetooth", "LoRaWAN", "Zigbee"),
            "Enables connectivity between remote devices and central hubs."
        ),
        UtilityItemData(
            "Middleware Layer",
            "Service management and data processing",
            "The hub for data storage, analysis, and decision-making. It manages device services and processes incoming streams.",
            listOf("Cloud Computing", "APIs", "Database Management", "Edge Computing"),
            "Central processing unit of a complete IoT ecosystem."
        ),
        UtilityItemData(
            "Application Layer",
            "User interface and specific services",
            "The top layer that delivers specific services to the user. It defines various applications in which IoT can be deployed.",
            listOf("Smart Home Apps", "Industrial Dashboards", "Healthcare Monitoring"),
            "Final stage where data provides value to the end user."
        )
    )
    UtilityList(layers, onClick)
}
