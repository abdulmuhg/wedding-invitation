package com.mrc.wedding.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeddingDTO(
    val id: Int,
    val slug: String,
    val brideFullName: String,
    val groomFullName: String,
    val eventDate: String,
    val events: List<EventDTO>,
    val gifts: List<GiftDTO>
)

@Serializable
data class EventDTO(
    val id: Int,
    val name: String,
    val eventDate: String,
    val location: String,
    val mapLink: String?,
    val description: String?
)

@Serializable
data class GiftDTO(
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val description: String?
)

@Serializable
data class WishDTO(
    val guestName: String,
    val message: String,
    val date: String
)