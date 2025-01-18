package com.mrc.wedding.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.mrc.wedding.models.tables.GalleryAlbums
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class AlbumDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val coverPhotoUrl: String,
    val orderIndex: Int
)

class GalleryAlbumRepository {
    fun createAlbum(
        weddingId: Int,
        title: String,
        description: String?,
        coverPhotoUrl: String,
        orderIndex: Int
    ): Int = transaction {
        GalleryAlbums.insert {
            it[this.weddingId] = weddingId
            it[this.title] = title
            it[this.description] = description
            it[this.coverPhotoUrl] = coverPhotoUrl
            it[this.orderIndex] = orderIndex
        }[GalleryAlbums.id]
    }

    fun getAlbumsByWeddingId(weddingId: Int): List<AlbumDTO> = transaction {
        GalleryAlbums
            .select { GalleryAlbums.weddingId eq weddingId }
            .orderBy(GalleryAlbums.orderIndex)
            .map { row ->
                AlbumDTO(
                    id = row[GalleryAlbums.id],
                    title = row[GalleryAlbums.title],
                    description = row[GalleryAlbums.description],
                    coverPhotoUrl = row[GalleryAlbums.coverPhotoUrl],
                    orderIndex = row[GalleryAlbums.orderIndex]
                )
            }
    }

    fun updateAlbum(
        albumId: Int,
        title: String? = null,
        description: String? = null,
        coverPhotoUrl: String? = null,
        orderIndex: Int? = null
    ): Boolean = transaction {
        val updateStatement = GalleryAlbums.update({ GalleryAlbums.id eq albumId }) { stmt ->
            title?.let { stmt[GalleryAlbums.title] = it }
            description?.let { stmt[GalleryAlbums.description] = it }
            coverPhotoUrl?.let { stmt[GalleryAlbums.coverPhotoUrl] = it }
            orderIndex?.let { stmt[GalleryAlbums.orderIndex] = it }
        }
        updateStatement > 0
    }

    fun deleteAlbum(albumId: Int): Boolean = transaction {
        GalleryAlbums.deleteWhere { GalleryAlbums.id eq albumId } > 0
    }
}