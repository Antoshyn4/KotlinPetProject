package org.kotlin.ktor.e2e

import org.kotlin.ktor.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals



class FullDeliveryCalculationTest {
    val listOfExistingVenue :List<String> = listOf("home-assignment-venue-helsinki", "home-assignment-venue-stockholm","home-assignment-venue-berlin","home-assignment-venue-tokyo")
    val mapofUserCoordinatesToVenueName :Map<String, String> = mapOf("home-assignment-venue-helsinki" to "user_lat=60.17094&user_lon=24.93087",
        "home-assignment-venue-stockholm" to "user_lat=59.345852&user_lon=18.033350",
        "home-assignment-venue-berlin" to "user_lat=52.507690187629365&user_lon=13.437304275034867",
        "home-assignment-venue-tokyo" to "user_lat=35.656781&user_lon=139.714329")

    /*{"total_price":1190,"small_order_surcharge":0,"cart_value":1000,"delivery":{"fee":190,"distance":177},"isDeliveryPossible":true}*/


    @Test
    fun testRoot() = testApplication {
        application {
            module(testing = true)
        }
        //Test delivery with user coordinates near the venues
        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + venueTestData.value).apply {
                print("For venue ${venueTestData} ")
                println(bodyAsText())
                assertEquals(HttpStatusCode.OK, status)
            }
        }

        //Test delivery with user coordinates at 0.0 0.0 so the delivery would be impossible
        mapofUserCoordinatesToVenueName.forEach { venueTestData ->
            client.get("/api/v1/delivery-order-price?venue_slug=" + venueTestData.key + "&cart_value=1000&" + "user_lat=0.0&user_lon=0.0").apply {
                print("\nFor venue ${venueTestData} ")
                assertEquals(HttpStatusCode.BadRequest, status)
                println(bodyAsText())
            }
        }
    }
}


