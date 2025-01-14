package com.mrc.wedding.repositories

import com.mrc.wedding.models.tables.Photos
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class PhotoDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val url: String,
    val category: String
)

class PhotoRepository {
    fun addPhoto(
        weddingId: Int,  // Add this parameter
        title: String,
        description: String?,
        url: String,
        category: String
    ): Int {
        return transaction {
            Photos.insert {
                it[Photos.weddingId] = weddingId  // Set the wedding_id
                it[Photos.title] = title
                it[Photos.description] = description
                it[Photos.url] = url
                it[Photos.category] = category
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }[Photos.id]
        }
    }

    fun getPhotosByCategory(category: String): List<PhotoDTO> = transaction {
        Photos.select { Photos.category eq category }
            .map { row ->
                PhotoDTO(
                    id = row[Photos.id],
                    title = row[Photos.title],
                    description = row[Photos.description],
                    url = row[Photos.url],
                    category = row[Photos.category]
                )
            }
    }
}