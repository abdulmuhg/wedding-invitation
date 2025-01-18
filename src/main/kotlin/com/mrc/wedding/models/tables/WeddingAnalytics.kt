package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object WeddingAnalytics : Table() {
    val id = integer("id").autoIncrement()
    val weddingId = integer("wedding_id").references(Weddings.id)
    val visitorIp = varchar("visitor_ip", 45)
    val visitDate = datetime("visit_date")
    val userAgent = text("user_agent").nullable()
    val eventType = varchar("event_type", 50) // PAGE_VIEW, RSVP, WISH, REACTION, etc.
    val referrer = text("referrer").nullable()
    val country = varchar("country", 100).nullable()
    val city = varchar("city", 100).nullable()
    val deviceType = varchar("device_type", 50).nullable() // MOBILE, DESKTOP, TABLET
    val sessionId = varchar("session_id", 100) // To track unique visits

    override val primaryKey = PrimaryKey(id)
}