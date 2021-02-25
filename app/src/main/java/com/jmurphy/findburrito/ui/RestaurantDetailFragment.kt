package com.jmurphy.findburrito.ui

import android.location.Geocoder
import android.location.GnssNavigationMessage
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionInflater
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView
import com.jmurphy.findburrito.R
import com.jmurphy.findburrito.RestaurantViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOError
import java.io.IOException


class RestaurantDetailFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = RestaurantDetailFragment()
    }

    private lateinit var viewModel: RestaurantViewModel
    private lateinit var mapView: MapView
    private lateinit var infoLayout: MaterialCardView
    private lateinit var errorMessage: TextView

    private val backPressedCallback =object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.restaurant_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(RestaurantViewModel::class.java)

        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)

        infoLayout = view.findViewById(R.id.info_card)
        errorMessage = view.findViewById(R.id.error_message)
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        parentFragment?.startPostponedEnterTransition()

        val slideTransition = Slide()
        slideTransition.slideEdge = Gravity.START

        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.isEnabled = false
        backPressedCallback.remove()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        viewModel.getSelectedRestaurant().observe(viewLifecycleOwner, { restaurant ->

            activity?.actionBar?.setTitle(restaurant.name)

            infoLayout.transitionName = restaurant.name
            infoLayout.findViewById<TextView>(R.id.address).setText(restaurant.address)
            infoLayout.findViewById<TextView>(R.id.price_rating).setText(restaurant.price)
            infoLayout.findViewById<TextView>(R.id.phone_number).setText(restaurant.phone)

            if (restaurant.coordinates != null){

                val latLng = LatLng(restaurant.coordinates!!.latitude, restaurant.coordinates!!.longitude)
                val position = CameraPosition.Builder().target(latLng).zoom(15f).build()
                val center = CameraUpdateFactory.newLatLng(latLng)

                googleMap?.apply {
                    addMarker(MarkerOptions().position(latLng).title(restaurant.name)).showInfoWindow()
                    moveCamera(CameraUpdateFactory.newCameraPosition(position))
                }
            }
            else{
                //show error message
                errorMessage.visibility = View.VISIBLE
            }

            startPostponedEnterTransition()

        })
    }
}