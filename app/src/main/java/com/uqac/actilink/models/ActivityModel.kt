package com.uqac.actilink.models

data class ActivityModel(
    val id: String = "",  // ID Firestore
    val title: String = "",
    val type: String = "",
    val dateTime: String = "",
    val location: String = "",
    val creatorId: String = "",
    val participants: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)