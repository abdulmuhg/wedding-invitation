package com.mrc.wedding.routes

import com.mrc.wedding.models.dto.AnalyticsEvent
import com.mrc.wedding.repositories.GeoInfo
import com.mrc.wedding.repositories.AnalyticsRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.analyticsRoutes() {
    val repository = AnalyticsRepository()

    route("/api/weddings/{weddingId}/analytics") {
        // Track event
        post("/events") {
            try {
                val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

                val event = call.receive<AnalyticsEvent>()

                // You might want to add IP geolocation service here
                val geoInfo = GeoInfo(
                    country = null,  // Implement IP geolocation
                    city = null
                )

                val id = repository.trackEvent(weddingId, event, geoInfo)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (e.message ?: "Unknown error occurred"))
                )
            }
        }

        // Get analytics
        get {
            try {
                val weddingId = call.parameters["weddingId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid wedding ID")

                val startDate = call.parameters["startDate"]?.let {
                    LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                } ?: LocalDateTime.now().minusMonths(1)

                val endDate = call.parameters["endDate"]?.let {
                    LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                } ?: LocalDateTime.now()

                val analytics = repository.getAnalytics(weddingId, startDate, endDate)
                call.respond(analytics)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to (e.message ?: "Unknown error occurred"))
                )
            }
        }
    }
}