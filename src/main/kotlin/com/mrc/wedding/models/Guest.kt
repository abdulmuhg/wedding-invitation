package com.mrc.wedding.models

import kotlinx.serialization.Serializable

@Serializable
data class Guest(
    val id: Int? = null,
    val name: String,
    val email: String,
    val invitationCode: String,
    val numberOfGuests: Int = 1,
    val isAttending: Boolean? = null,
    val message: String? = null
)