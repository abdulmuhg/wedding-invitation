package com.mrc.wedding

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        // Configure the test application
        application {
            // Call the module function that sets up your routes
            module()
        }

        // Perform the test
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Welcome to Our Wedding!", bodyAsText())
        }
    }
}