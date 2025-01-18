package com.mrc.wedding.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Weddings : Table() {
    val id = integer("id").autoIncrement()
    val slug = varchar("slug", 100).uniqueIndex()  // for URL: mybrand.com/wedding/john-and-jane
    val brideFirstName = varchar("bride_first_name", 100)
    val brideLastName = varchar("bride_last_name", 100)
    val groomFirstName = varchar("groom_first_name", 100)
    val groomLastName = varchar("groom_last_name", 100)
    val eventDate = datetime("event_date")
    val status = varchar("status", 20).default("ACTIVE")  // ACTIVE, INACTIVE, EXPIRED
    val coverPhotoUrl = varchar("cover_photo_url", 500).nullable()
    val quranVerse = text("quran_verse").nullable()  // For the Quran verse shown
    val liveStreamUrl = varchar("live_stream_url", 500).nullable()
    val websiteTheme = varchar("website_theme", 50).default("NETFLIX")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}