package com.mrc.wedding

import com.mrc.wedding.config.DatabaseConfig
import com.mrc.wedding.routes.giftRoutes
import com.mrc.wedding.routes.guestRoutes
import com.mrc.wedding.routes.photoRoutes
import com.mrc.wedding.services.AWSConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Initialize database
    DatabaseConfig.init()

    val awsConfig = AWSConfig.fromEnv()

    // Install plugins
    install(ContentNegotiation) {
        json()
    }

    // Configure routing
    routing {
        get("/") {
            call.respondText("Welcome to Our Wedding!")
        }
        guestRoutes()
        photoRoutes(awsConfig)
        giftRoutes()
    }
}