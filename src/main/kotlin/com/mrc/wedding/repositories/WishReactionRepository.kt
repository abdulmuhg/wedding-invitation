package com.mrc.wedding.repositories

import com.mrc.wedding.models.tables.WishReactions
import com.mrc.wedding.utils.RateLimiter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class WishReactionRepository {
    companion object {
        private const val MAX_REACTIONS_PER_HOUR = 50
        private const val RATE_LIMIT_WINDOW_MINUTES = 60
    }

    fun addReaction(
        wishId: Int,
        guestName: String,
        reactionType: String
    ): Result<Int> = transaction {
        // Check rate limit
        val rateKey = "reaction:$guestName"
        if (!RateLimiter.checkRateLimit(rateKey, MAX_REACTIONS_PER_HOUR, RATE_LIMIT_WINDOW_MINUTES)) {
            return@transaction Result.failure(
                Exception("Rate limit exceeded. Please try again later.")
            )
        }

        try {
            // First try to update existing reaction
            val updated = WishReactions.update({
                (WishReactions.wishId eq wishId) and
                        (WishReactions.guestName eq guestName)
            }) {
                it[WishReactions.reactionType] = reactionType
            }

            val id = if (updated > 0) {
                // Return existing reaction ID
                WishReactions
                    .select {
                        (WishReactions.wishId eq wishId) and
                                (WishReactions.guestName eq guestName)
                    }
                    .first()[WishReactions.id]
            } else {
                // Create new reaction
                WishReactions.insert {
                    it[this.wishId] = wishId
                    it[this.guestName] = guestName
                    it[this.reactionType] = reactionType
                    it[createdAt] = LocalDateTime.now()
                }[WishReactions.id]
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeReaction(
        wishId: Int,
        guestName: String
    ): Boolean = transaction {
        WishReactions.deleteWhere {
            (WishReactions.wishId eq  wishId) and
                    (WishReactions.guestName eq guestName)
        } > 0
    }

    fun getReactionsForWish(wishId: Int): Map<String, Int> = transaction {
        WishReactions
            .slice(WishReactions.reactionType, WishReactions.reactionType.count())
            .select { WishReactions.wishId eq wishId }
            .groupBy(WishReactions.reactionType)
            .associate {
                it[WishReactions.reactionType] to it[WishReactions.reactionType.count()].toInt()
            }
    }

    fun getGuestReactionForWish(
        wishId: Int,
        guestName: String
    ): String? = transaction {
        WishReactions
            .select {
                (WishReactions.wishId eq wishId) and
                        (WishReactions.guestName eq guestName)
            }
            .map { it[WishReactions.reactionType] }
            .singleOrNull()
    }
}