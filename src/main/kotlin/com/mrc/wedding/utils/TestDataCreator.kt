package com.mrc.wedding.utils

import com.mrc.wedding.config.DatabaseConfig
import com.mrc.wedding.repositories.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

fun main() {
    // Initialize database
    DatabaseConfig.init()

    // Clear existing data and recreate tables
//    transaction {
//        SchemaUtils.drop(Guests, Photos, WeddingEvents, WeddingGifts, Weddings)
//        SchemaUtils.create(Guests, Photos, WeddingEvents, WeddingGifts, Weddings)
//    }

    // Initialize repositories
    val weddingRepo = WeddingEventRepository()
    val guestRepo = GuestRepository()
    val photoRepo = PhotoRepository()
    val giftRepo = WeddingGiftRepository()
    val wishRepo = WishRepository()

    // Create test data
    transaction {
        try {
            // Create a wedding
            val weddingId = weddingRepo.createWedding(
                brideFirstName = "Jane",
                brideLastName = "Doe",
                groomFirstName = "John",
                groomLastName = "Smith",
                eventDate = LocalDateTime.now().plusMonths(2),
                slug = "john-and-jane-2024"
            )
            println("Created wedding with ID: $weddingId")

            // Create gallery albums
            val albumRepo = GalleryAlbumRepository()
            val albumNames = listOf("Perfect Date", "Our Story", "Pre-Wedding")
            val albumIds = albumNames.mapIndexed { index, name ->
                albumRepo.createAlbum(
                    weddingId = weddingId,
                    title = name,
                    description = "Photos from our $name",
                    coverPhotoUrl = "https://example.com/cover-$index.jpg",
                    orderIndex = index
                )
            }

            // Create events
            listOf(
                Triple("Holy Matrimony", 10, "St. Mary's Church"),
                Triple("Wedding Reception", 17, "Grand Ballroom, Luxury Hotel")
            ).forEach { (name, hour, location) ->
                weddingRepo.createEvent(
                    weddingId = weddingId,
                    name = name,
                    eventDate = LocalDateTime.now().plusMonths(2).withHour(hour),
                    location = location,
                    mapLink = "https://goo.gl/maps/${UUID.randomUUID()}",
                    description = "$name ceremony"
                )
                println("Created event: $name")
            }

            // Create gift
            giftRepo.createGift(
                weddingId = weddingId,
                bankName = "Chase Bank",
                accountNumber = "1234567890",
                accountHolder = "John Smith",
                description = "Wedding gift"
            )
            println("Created gift record")

            // Create guests
            listOf(
                Triple("Alice Johnson", "alice@email.com", 2),
                Triple("Bob Wilson", "bob@email.com", 1),
                Triple("Carol Brown", "carol@email.com", 3),
                Triple("David Lee", "david@email.com", 2)
            ).forEach { (name, email, count) ->
                val guestId = guestRepo.createGuest(
                    weddingId = weddingId,
                    name = name,
                    email = email,
                    invitationCode = "${UUID.randomUUID()}".take(6).uppercase()
                )
                println("Created guest $name with ID: $guestId")
            }

            // Create photos
            val categories = listOf("PRE_WEDDING", "CEREMONY", "RECEPTION")
            categories.forEach { category ->
                repeat(3) { index ->
                    photoRepo.addPhoto(
                        weddingId = weddingId,
                        title = "Test Photo ${index + 1}",
                        description = "Test photo in category $category",
                        url = "https://com-mrc-wedding-invitation-dev.s3.ap-southeast-1.amazonaws.com/photos/${UUID.randomUUID()}",
                        category = category,
                        albumId = albumIds.random(),
                        orderIndex = index
                    )
                }
                println("Created photo for category: $category")
            }

            listOf(
                "Congratulations!" to "Best wishes for your future together!",
                "Happy Wedding!" to "May your love grow stronger each day",
                "Wonderful News!" to "So happy for both of you!"
            ).forEach { (name, message) ->
                wishRepo.createWish(
                    weddingId = weddingId,
                    guestName = name,
                    message = message
                )
            }

            println("Test data creation completed successfully!")
        } catch (e: Exception) {
            println("Error creating test data: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}