package model

import io.ktor.server.routing.*
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.*

/**
 * A factory interface responsible for creating objects from JSON data
 * extracted from a [RoutingCall].
 *
 * @param T The type of object to be created by the factory.
 */
interface Factory<T> {
    suspend fun createFromJson(call: RoutingCall): T?
}

/**
 * Represents a customer order that contains details such as venue information,
 * cart value, and the user's geographical coordinates. It provides methods
 * to calculate delivery distance, delivery fee, and generate a response object.
 *
 * @property venueSlug A name of the venue from which the order is placed.
 * @property cartValue The total value of items in the cart.
 * @property userLat The latitude of the user placing the order.
 * @property userLon The longitude of the user placing the order.
 */
data class Order(
    private val venueSlug: String,
    private val cartValue : Int,
    private val userLat : Double,
    private val userLon : Double
) {

    private var deliveryFee : Int = -1
    var totalOrderPrice : Int = -1
    private var smallOrderSurcharge : Int = 0
    private var distanceToVenue : Int = -1

    /**
     * Companion object implementing [Factory] to create an [Order] instance from JSON data
     * extracted from a [RoutingCall].
     */
    companion object : Factory<Order>{

        suspend override fun createFromJson(call: RoutingCall): Order? {

            val venueSlug = call.request.queryParameters["venue_slug"]
            val cartValue = call.request.queryParameters["cart_value"]?.toIntOrNull()
            val userLat = call.request.queryParameters["user_lat"]?.toDoubleOrNull()
            val userLon = call.request.queryParameters["user_lon"]?.toDoubleOrNull()

            if (venueSlug != null && cartValue != null  && userLat != null  && userLon != null) {
                return Order(venueSlug, cartValue, userLat, userLon)
            }
            return null
        }
    }

    /**
     * Calculates the total price of this order, which includes:
     * - Delivery fee, computed based on distance and venue data.
     * - Small order surcharge, if the [cartValue] is below the venue's threshold.
     *
     * The result of these calculations is stored in the corresponding properties,
     * and can later be retrieved through [makeResponse].
     *
     * @throws Exception If there's any issue retrieving or parsing the venue data.
     */
    suspend fun calculateTotalPrice(testing: Boolean){
        val deliveryVenue : Venue = Venue(venueSlug)

        try {
            deliveryVenue.fillVenueData(testing)
        } catch (e: Exception){
            throw Exception(e)
        }

        // Determines the distance to the venue in meters and stores it in [distanceToVenue].
        calculateDistance(deliveryVenue.coordinates!!)


        var a : Int = -1
        var b : Double = -1.0
        var correctDistanceRange : Boolean = false


        // Iterates over the distance ranges to find the appropriate fee parameters (a, b).
        for (indexOfDistanceRanges in deliveryVenue.distanceRanges!!.indices){

            if (deliveryVenue.distanceRanges!![indexOfDistanceRanges] != ","){
                parseToJsonElement('{' + deliveryVenue.distanceRanges!![indexOfDistanceRanges] + '}').jsonObject.forEach{distanceRangeObject ->

                    when (distanceRangeObject.key){
                        DistanceRangeObjectFields.min.name ->
                            if(this.distanceToVenue >= distanceRangeObject.value.toString().toInt()) correctDistanceRange = true
                        DistanceRangeObjectFields.max.name ->
                            if(this.distanceToVenue < distanceRangeObject.value.toString().toInt() && correctDistanceRange) correctDistanceRange = true else correctDistanceRange = false
                        DistanceRangeObjectFields.a.name ->
                            if (correctDistanceRange) a = distanceRangeObject.value.toString().toInt()
                        DistanceRangeObjectFields.b.name ->
                            if (correctDistanceRange) b = distanceRangeObject.value.toString().toDouble()
                    }
                }
                if (correctDistanceRange){
                    break
                }
            }

        }

        // If we have distance to the venue which is not exceed the distance ranges, compute the final delivery fee and total order price.
        if (a != -1 && b!= -1.0){
            this.deliveryFee = (deliveryVenue.basePrice!! + a + b*this.distanceToVenue/10).roundToInt()

            // Calculate small order surcharge if the cart value is below the threshold.
            if (deliveryVenue.orderMinimumNoSurcharge!! > this.cartValue){
                this.smallOrderSurcharge = deliveryVenue.orderMinimumNoSurcharge!! - this.cartValue
            }

            this.totalOrderPrice = this.cartValue + this.smallOrderSurcharge + this.deliveryFee
        }
    }


    /**
     * Calculates the distance in meters between the user's location and the venue's coordinates
     * using the Haversine formula, and stores the result in [distanceToVenue].
     *
     * @param venueCoordinates A list of two strings representing the latitude and longitude of the venue.
     */
    fun calculateDistance(venueCoordinates : List<String>){
        val earthRadius = 6371.0 * 1000 // Earth radius in metres

        if (venueCoordinates.size == 2){

            val venueLatitude =  venueCoordinates[0].replace("[","").replace("]","").toDouble()
            val venueLongitude =  venueCoordinates[1].replace("[","").replace("]","").toDouble()

            val latDistance = Math.toRadians(venueLatitude - this.userLat)
            val lonDistance = Math.toRadians(venueLongitude - this.userLon)

            val formulaRelatedVariable = sin(latDistance / 2).pow(2) +
                    cos(Math.toRadians(this.userLat)) * cos(Math.toRadians(venueLatitude)) *
                    sin(lonDistance / 2).pow(2)

            val centralAngleBetweenTwoPoints = 2 * atan2(sqrt(formulaRelatedVariable), sqrt(1 - formulaRelatedVariable))

            this.distanceToVenue = (earthRadius * centralAngleBetweenTwoPoints).roundToInt()

        }
    }


    /**
     * Constructs a [Response] object reflecting the state of this order after price calculations.
     * This includes:
     * - [total_price]: Sum of cart value, small order surcharge, and delivery fee.
     * - [small_order_surcharge]: The surcharge for small orders, if any.
     * - [cart_value]: The original cart value of the order.
     * - [delivery]: An instance of [Delivery] holding the delivery fee and distance.
     * - [isDeliveryPossible]: A boolean indicating delivery possibility accounting distance
     *
     * @return A [Response] object containing the order's final state and pricing details.
     */
    fun makeResponse() : Response{
        val response : Response = Response(
            total_price = this.totalOrderPrice,
            small_order_surcharge = this.smallOrderSurcharge,
            cart_value = this.cartValue,
            delivery = Delivery(fee = this.deliveryFee, distance = this.distanceToVenue)
        )
        return response
    }
}