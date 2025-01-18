package com.mrc.wedding.repositories

import com.mrc.wedding.models.dto.WishDTO
import com.mrc.wedding.models.tables.WeddingWishes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class WishRepository {
    fun createWish(
        weddingId: Int,
        guestName: String,
        message: String
    ): Int = transaction {
        WeddingWishes.insert {
            it[this.weddingId] = weddingId
            it[this.guestName] = guestName
            it[this.message] = message
            it[createdAt] = LocalDateTime.now()
        }[WeddingWishes.id]
    }

    fun getWishesByWeddingId(weddingId: Int, currentGuestName: String? = null): List<WishDTO> = transaction {
        val reactionRepo = WishReactionRepository()

        WeddingWishes
            .select { WeddingWishes.weddingId eq weddingId }
            .orderBy(WeddingWishes.createdAt, SortOrder.DESC)
            .map { row ->
                val wishId = row[WeddingWishes.id]
                WishDTO(
                    id = wishId,
                    guestName = row[WeddingWishes.guestName],
                    message = row[WeddingWishes.message],
                    date = row[WeddingWishes.createdAt].toString(),
                    reactions = reactionRepo.getReactionsForWish(wishId),
                    myReaction = currentGuestName?.let {
                        reactionRepo.getGuestReactionForWish(wishId, it)
                    }
                )
            }
    }
}