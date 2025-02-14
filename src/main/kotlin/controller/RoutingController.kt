package controller

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.routingController() {
    get("/api/v1/delivery-order-price") {
        println("check")
    }
    get ("/*"){
        call.response.status(HttpStatusCode(404, "Not found"))
        call.respond("Bad Request, Not found");
    }
}