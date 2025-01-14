package com.mrc.wedding.repositories

import com.mrc.wedding.models.dto.GiftDTO
import com.mrc.wedding.models.tables.WeddingGifts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class WeddingGiftRepository {
    fun createGift(
        weddingId: Int,
        bankName: String,
        accountNumber: String,
        accountHolder: String,
        description: String? = null
    ): Int = transaction {
        WeddingGifts.insert {
            it[this.weddingId] = weddingId
            it[this.bankName] = bankName
            it[this.accountNumber] = accountNumber
            it[this.accountHolder] = accountHolder
            it[this.description] = description
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }[WeddingGifts.id]
    }

    fun getGiftsByWeddingId(weddingId: Int): List<GiftDTO> = transaction {
        WeddingGifts
            .select { WeddingGifts.weddingId eq weddingId }
            .map { row ->
                GiftDTO(
                    bankName = row[WeddingGifts.bankName],
                    accountNumber = row[WeddingGifts.accountNumber],
                    accountHolder = row[WeddingGifts.accountHolder],
                    description = row[WeddingGifts.description]
                )
            }
    }

    fun updateGift(
        giftId: Int,
        bankName: String? = null,
        accountNumber: String? = null,
        accountHolder: String? = null,
        description: String? = null
    ): Boolean = transaction {
        val updateStatement = WeddingGifts.update({ WeddingGifts.id eq giftId }) { stmt ->
            bankName?.let { stmt[WeddingGifts.bankName] = it }
            accountNumber?.let { stmt[WeddingGifts.accountNumber] = it }
            accountHolder?.let { stmt[WeddingGifts.accountHolder] = it }
            description?.let { stmt[WeddingGifts.description] = it }
            stmt[updatedAt] = LocalDateTime.now()
        }
        updateStatement > 0
    }

    fun deleteGift(giftId: Int): Boolean = transaction {
        WeddingGifts.deleteWhere { WeddingGifts.id eq  giftId } > 0
    }

    fun getGiftById(giftId: Int): GiftDTO? = transaction {
        WeddingGifts
            .select { WeddingGifts.id eq giftId }
            .map { row ->
                GiftDTO(
                    bankName = row[WeddingGifts.bankName],
                    accountNumber = row[WeddingGifts.accountNumber],
                    accountHolder = row[WeddingGifts.accountHolder],
                    description = row[WeddingGifts.description]
                )
            }
            .singleOrNull()
    }
}