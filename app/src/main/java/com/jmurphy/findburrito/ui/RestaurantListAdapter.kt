package com.jmurphy.findburrito.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jmurphy.findburrito.R

import com.jmurphy.findburrito.data.RestaurantContent.RestaurantItem

/**
 * [RecyclerView.Adapter] that can display a [RestaurantItem].
 * TODO: Replace the implementation with code for your data type.
 */
class RestaurantListAdapter(val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>() {

    private val values = ArrayList<GetRestaurantsQuery.Business?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)
        return RestaurantViewHolder(view)
    }

    fun setData(data : List<GetRestaurantsQuery.Business?>){
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item?.name
        holder.address.text = item?.location?.formatted_address
        holder.phone.text = item?.phone
        holder.price.text = item?.price

        holder.itemView.transitionName = item?.name

        holder.bind(item!!, itemClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.biz_name)
        val address: TextView = view.findViewById(R.id.address)
        val phone: TextView = view.findViewById(R.id.phone_number)
        val price: TextView = view.findViewById(R.id.price_rating)


        override fun toString(): String {
            return name.text.toString()
        }

        fun bind(restaurant: GetRestaurantsQuery.Business, clickListener: OnItemClickListener){
            itemView.setOnClickListener{
                clickListener.onItemClicked(itemView, restaurant)

            }
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(view: View, restaurant: GetRestaurantsQuery.Business)
}
