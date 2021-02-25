package com.jmurphy.findburrito.data

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
object RestaurantContent {

    /**
     * An item representing a restaurant.
     */
    data class RestaurantItem(val name: String, val address: String, val price: String, val phone: String, var coordinates: Location? = null) {
        override fun toString(): String = name
    }

    data class Location(val latitude: Double, val longitude: Double)
}