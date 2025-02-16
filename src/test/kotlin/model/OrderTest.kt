package org.kotlin.ktor.model

import org.junit.Test
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.kotlin.ktor.module
import kotlin.test.assertEquals

class OrderTest {
    val mapofUserCoordinatesToVenueName :Map<String, String> = mapOf("home-assignment-venue-helsinki" to "user_lat=60.17094&user_lon=24.93087",
        "home-assignment-venue-stockholm" to "user_lat=59.345852&user_lon=18.033350",
        "home-assignment-venue-berlin" to "user_lat=52.507690187629365&user_lon=13.437304275034867",
        "home-assignment-venue-tokyo" to "user_lat=35.656781&user_lon=139.714329")

    @Test
    fun testCreateFromJSON() = testApplication{
        application {
            module(testing = true)
        }

        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + "user_lat=0.0&user_lon=0.0").apply {
                assertEquals(HttpStatusCode.BadRequest, status)
            }
        }
    }

    @Test
    fun calculateTotalPriceTest() = testApplication {
        application {
            module(testing = true)
        }

        client.get("/api/v1/delivery-order-price?venue_slug=" + "home-assignment-venue-helsinki" + "&cart_value=1000&" + "user_lat=60.17094&user_lon=24.93087")
            .apply {
                assertEquals("1190", bodyAsText().substringAfter("total_price\":").substringBefore(","))
            }
    }

    @Test
    fun calculateDistanceTest() = testApplication {
        application {
            module(testing = true)
        }

        client.get("/api/v1/delivery-order-price?venue_slug=" + "home-assignment-venue-helsinki" + "&cart_value=1000&" + "user_lat=60.17094&user_lon=24.93087")
            .apply {
                assertEquals("177", bodyAsText().substringAfter("distance\":").substringBefore("}}"))
            }
    }

    @Test
    fun makeResponseTest() = testApplication {
        application {
            module(testing = true)
        }

        client.get("/api/v1/delivery-order-price?venue_slug=" + "home-assignment-venue-helsinki" + "&cart_value=1000&" + "user_lat=60.17094&user_lon=24.93087")
            .apply {
                assertEquals(true, bodyAsText().contains("total_price\":"))
                assertEquals(true, bodyAsText().contains("small_order_surcharge\":"))
                assertEquals(true, bodyAsText().contains("cart_value\":"))
                assertEquals(true, bodyAsText().contains("distance\":"))
                assertEquals(true, bodyAsText().contains("fee\":"))
            }
    }

}