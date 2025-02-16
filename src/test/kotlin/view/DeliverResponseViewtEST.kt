package view

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import org.kotlin.ktor.module
import kotlin.test.assertEquals

class DeliverResponseViewtEST {
    val mapofUserCoordinatesToVenueName :Map<String, String> = mapOf("home-assignment-venue-helsinki" to "user_lat=60.17094&user_lon=24.93087",
        "home-assignment-venue-stockholm" to "user_lat=59.345852&user_lon=18.033350",
        "home-assignment-venue-berlin" to "user_lat=52.507690187629365&user_lon=13.437304275034867",
        "home-assignment-venue-tokyo" to "user_lat=35.656781&user_lon=139.714329")


    @Test
    fun testRoot() = testApplication {
        application {
            module(testing = true)
        }
        //Test delivery with user coordinates near the venues
        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + venueTestData.value).apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }

        //Test delivery with user coordinates at 0.0 0.0 so the delivery would be impossible
        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + "user_lat=0.0&user_lon=0.0").apply {
                assertEquals(HttpStatusCode.BadRequest, status)
            }
        }

        //Test with incorrect venue name
        client.get("/api/v1/delivery-order-price?venue_slug=" + "INCORRECT_VENUE" + "&cart_value=1000&" + "user_lat=0.0&user_lon=0.0").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}