package view

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Order

/**
 * @param call The [RoutingCall] containing request and response objects.
 * @param newOrder The [Order] object for which to calculate and send a response. May be `null`.
 */
suspend fun sendResponse(call: RoutingCall, newOrder : Order?, testing: Boolean){
    //If [newOrder] is `null`, a "Missing or invalid parameters" message is returned.
    if (newOrder != null) {
        try {

            newOrder.calculateTotalPrice(testing)

            val response = newOrder.makeResponse()

            if (response.delivery.fee >= 0) {
                /**If all the passed data is correct and delivery is possible (distance is above threshold), a 200 response with the [Order] data is returned.*/
                call.response.status(HttpStatusCode(200, "Delivery price was calculated successfully"))
                call.respond(response)
            } else {
                /** - If delivery is not possible, a 400 response is returned, indicating the reason - distance.*/
                call.response.status(HttpStatusCode(400, "Delivery is not possible"))
                call.respond("Delivery is not possible because client is too far, distance is ${response.delivery.distance} metres");
            }

        }catch  (e: Exception) {
            call.response.status(HttpStatusCode(404, "Home Assignment Api Bad request ${e.message}"))
            call.respond("Home Assignment Api Bad request ${e.message}")
        }
    }
    else call.respondText("Missing or invalid parameters", status = HttpStatusCode.BadRequest)

}