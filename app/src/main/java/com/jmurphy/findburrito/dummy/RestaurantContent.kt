package com.jmurphy.findburrito.dummy

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
    data class RestaurantItem(val name: String, val address: String, val price: String, val phone: String) {
        override fun toString(): String = name
    }
}