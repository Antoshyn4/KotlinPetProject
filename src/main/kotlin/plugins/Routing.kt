package org.kotlin.ktor

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import controller.routingController

fun Application.configureRouting(testing: Boolean) {
    routing {
        routingController(testing)
    }
}
