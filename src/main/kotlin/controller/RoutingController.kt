package controller


import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Order
import view.sendResponse


fun Route.routingController(testing: Boolean) {
    get("/api/v1/delivery-order-price") {
        /**
         * Handles GET requests to `/api/v1/delivery-order-price`.
         *
         * Retrieves the delivery order price by parsing the request and using the
         * [Order.createFromJson] method to create an [Order] instance from the incoming request JSON.
         * The response is sent using the [sendResponse] utility.
         *
         * @throws BadRequestException if the request JSON is invalid.
         */
        sendResponse(call, Order.createFromJson(call), testing)
    }
    get ("/*"){
        call.response.status(HttpStatusCode(404, "Not found"))
        call.respond("Bad Request, Not found");
    }
}