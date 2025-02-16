package org.kotlin.ktor.controller
import controller.routingController
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.kotlin.ktor.module
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingControllerTest {
    val mapofUserCoordinatesToVenueName :Map<String, String> = mapOf("home-assignment-venue-helsinki" to "user_lat=60.17094&user_lon=24.93087",
        "home-assignment-venue-stockholm" to "user_lat=59.345852&user_lon=18.033350",
        "home-assignment-venue-berlin" to "user_lat=52.507690187629365&user_lon=13.437304275034867",
        "home-assignment-venue-tokyo" to "user_lat=35.656781&user_lon=139.714329")


    @Test
    fun testRoutingFunction() = testApplication {
        application {
            module(testing = true)
        }

        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + venueTestData.value).apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }
        client.get("/").apply{
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}