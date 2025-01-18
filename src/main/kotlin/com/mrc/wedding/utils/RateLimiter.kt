package com.mrc.wedding.utils

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

object RateLimiter {
    private data class RateLimit(
        val attempts: Int,
        val firstAttemptTime: LocalDateTime
    )

    private val reactionLimits = ConcurrentHashMap<String, RateLimit>()
    
    // Clear old entries every hour
    init {
        Thread {
            while (true) {
                Thread.sleep(3600000) // 1 hour
                clearOldEntries()
            }
        }.start()
    }

    fun checkRateLimit(key: String, maxAttempts: Int, windowMinutes: Int): Boolean {
        val now = LocalDateTime.now()
        val limit = reactionLimits.compute(key) { _, current ->
            when {
                current == null -> RateLimit(1, now)
                current.firstAttemptTime.plusMinutes(windowMinutes.toLong()) < now -> 
                    RateLimit(1, now)
                current.attempts >= maxAttempts -> current
                else -> current.copy(attempts = current.attempts + 1)
            }
        }
        return limit!!.attempts <= maxAttempts
    }

    private fun clearOldEntries() {
        val now = LocalDateTime.now()
        reactionLimits.entries.removeIf { 
            it.value.firstAttemptTime.plusMinutes(60) < now 
        }
    }
}