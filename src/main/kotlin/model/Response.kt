package model

import kotlinx.serialization.Serializable

/**
 * Represents the response returned after calculating the total order price, delivery fee,
 * and other relevant order details.
 *
 * @property total_price The total price of the order, including delivery fees and any surcharges.
 * @property small_order_surcharge The surcharge applied if the cart value is below a minimum threshold.
 * @property cart_value The original total value of the items in the cart.
 * @property delivery An instance of [Delivery] containing the delivery fee and distance.
 * @property isDeliveryPossible A boolean indicating delivery possibility accounting distance
 */
@Serializable
data class Response(
    private val total_price: Int,
    private val small_order_surcharge: Int,
    private val cart_value: Int,
    val delivery: Delivery
)

/**
 * Holds delivery-related information for an order, such as the delivery fee and distance.
 *
 * @property fee The calculated delivery fee for the order.
 * @property distance The distance from the user's location to the venue, in meters.
 */

@Serializable
data class Delivery(
    val fee: Int,
    val distance: Int
)