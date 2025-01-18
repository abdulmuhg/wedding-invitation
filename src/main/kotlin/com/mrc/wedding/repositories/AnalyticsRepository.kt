package com.mrc.wedding.repositories

import com.mrc.wedding.models.dto.*
import com.mrc.wedding.models.tables.WeddingAnalytics
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsRepository {
    
    fun trackEvent(
        weddingId: Int,
        event: AnalyticsEvent,
        geoInfo: GeoInfo?
    ): Int = transaction {
        WeddingAnalytics.insert {
            it[this.weddingId] = weddingId
            it[visitorIp] = event.visitorIp
            it[visitDate] = LocalDateTime.now()
            it[userAgent] = event.userAgent
            it[eventType] = event.eventType
            it[referrer] = event.referrer
            it[country] = geoInfo?.country
            it[city] = geoInfo?.city
            it[deviceType] = detectDeviceType(event.userAgent)
            it[sessionId] = event.sessionId
        }[WeddingAnalytics.id]
    }

    fun getAnalytics(
        weddingId: Int,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): AnalyticsDTO = transaction {
        // Total visits
        val totalVisits = WeddingAnalytics
            .select { 
                (WeddingAnalytics.weddingId eq weddingId) and
                (WeddingAnalytics.visitDate.between(startDate, endDate))
            }
            .count()

        // Unique visitors
        val uniqueVisitors = WeddingAnalytics
            .slice(WeddingAnalytics.visitorIp)
            .select {
                (WeddingAnalytics.weddingId eq weddingId) and
                (WeddingAnalytics.visitDate.between(startDate, endDate))
            }
            .withDistinct()
            .count()

        // Device breakdown
        val deviceBreakdown = WeddingAnalytics
            .slice(WeddingAnalytics.deviceType, WeddingAnalytics.deviceType.count())
            .select {
                (WeddingAnalytics.weddingId eq weddingId) and
                (WeddingAnalytics.visitDate.between(startDate, endDate))
            }
            .groupBy(WeddingAnalytics.deviceType)
            .associate {
                (it[WeddingAnalytics.deviceType] ?: "UNKNOWN") to it[WeddingAnalytics.deviceType.count()].toInt()
            }

        // Top countries
        val topCountries = WeddingAnalytics
            .slice(WeddingAnalytics.country, WeddingAnalytics.country.count())
            .select {
                (WeddingAnalytics.weddingId eq weddingId) and
                (WeddingAnalytics.visitDate.between(startDate, endDate)) and
                (WeddingAnalytics.country.isNotNull())
            }
            .groupBy(WeddingAnalytics.country)
            .map {
                CountryStats(
                    country = it[WeddingAnalytics.country] ?: "Unknown",
                    visits = it[WeddingAnalytics.country.count()].toInt()
                )
            }
            .sortedByDescending { it.visits }
            .take(10)

        // Daily stats
        val dailyVisits = mutableListOf<DailyStats>()
        var currentDate = startDate.toLocalDate()
        val endLocalDate = endDate.toLocalDate()

        while (!currentDate.isAfter(endLocalDate)) {
            val dayStart = currentDate.atStartOfDay()
            val dayEnd = currentDate.plusDays(1).atStartOfDay()

            val dailyTotal = WeddingAnalytics
                .select {
                    (WeddingAnalytics.weddingId eq weddingId) and
                    (WeddingAnalytics.visitDate.between(dayStart, dayEnd))
                }
                .count()

            val dailyUnique = WeddingAnalytics
                .slice(WeddingAnalytics.visitorIp)
                .select {
                    (WeddingAnalytics.weddingId eq weddingId) and
                    (WeddingAnalytics.visitDate.between(dayStart, dayEnd))
                }
                .withDistinct()
                .count()

            dailyVisits.add(
                DailyStats(
                    date = currentDate.format(DateTimeFormatter.ISO_DATE),
                    visits = dailyTotal.toInt(),
                    uniqueVisitors = dailyUnique.toInt()
                )
            )
            currentDate = currentDate.plusDays(1)
        }

        // Event breakdown
        val eventBreakdown = WeddingAnalytics
            .slice(WeddingAnalytics.eventType, WeddingAnalytics.eventType.count())
            .select {
                (WeddingAnalytics.weddingId eq weddingId) and
                (WeddingAnalytics.visitDate.between(startDate, endDate))
            }
            .groupBy(WeddingAnalytics.eventType)
            .associate {
                it[WeddingAnalytics.eventType] to it[WeddingAnalytics.eventType.count()].toInt()
            }

        AnalyticsDTO(
            totalVisits = totalVisits.toInt(),
            uniqueVisitors = uniqueVisitors.toInt(),
            deviceBreakdown = deviceBreakdown,
            topCountries = topCountries,
            dailyVisits = dailyVisits,
            eventBreakdown = eventBreakdown
        )
    }

    private fun detectDeviceType(userAgent: String?): String {
        if (userAgent == null) return "UNKNOWN"
        return when {
            userAgent.contains("Mobile") -> "MOBILE"
            userAgent.contains("Tablet") -> "TABLET"
            else -> "DESKTOP"
        }
    }
}

data class GeoInfo(
    val country: String?,
    val city: String?
)