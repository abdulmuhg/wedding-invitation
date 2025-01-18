package com.mrc.wedding.utils

import com.mrc.wedding.config.DatabaseConfig
import com.mrc.wedding.models.tables.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    // Initialize database connection
    DatabaseConfig.init()

    transaction {
        try {
            // Drop entire schema and recreate it
            val dropSchema = "DROP SCHEMA public CASCADE"
            val createSchema = "CREATE SCHEMA public"
            val grantPrivileges = """
                GRANT ALL ON SCHEMA public TO wedding_user;
                GRANT ALL ON SCHEMA public TO public;
            """.trimIndent()

            exec(dropSchema)
            println("Dropped schema")

            exec(createSchema)
            println("Created new schema")

            exec(grantPrivileges)
            println("Granted privileges")

            // Create all tables in correct order
            SchemaUtils.create(
                Weddings,
                Guests,
                WeddingEvents,
                WeddingGifts,
                GalleryAlbums,
                Photos,
                WeddingWishes,
                WishReactions,
                WeddingAnalytics,
            )
            println("Created all tables")

            println("Database migration completed successfully!")
        } catch (e: Exception) {
            println("Error during migration: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}