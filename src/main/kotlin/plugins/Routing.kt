package org.kotlin.ktor

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import controller.RoutingController

fun Application.configureRouting() {
    routing {
        RoutingController()
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
