package com.mrc.wedding.models.dto

@kotlinx.serialization.Serializable
data class AnalyticsDTO(
    val totalVisits: Int,
    val uniqueVisitors: Int,
    val deviceBreakdown: Map<String, Int>,
    val topCountries: List<CountryStats>,
    val dailyVisits: List<DailyStats>,
    val eventBreakdown: Map<String, Int>
)

@kotlinx.serialization.Serializable
data class CountryStats(
    val country: String,
    val visits: Int
)

@kotlinx.serialization.Serializable
data class DailyStats(
    val date: String,
    val visits: Int,
    val uniqueVisitors: Int
)

@kotlinx.serialization.Serializable
data class AnalyticsEvent(
    val eventType: String,
    val visitorIp: String,
    val userAgent: String?,
    val referrer: String?,
    val sessionId: String
)