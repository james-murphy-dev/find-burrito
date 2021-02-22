package com.jmurphy.findburrito.ui

import android.location.Geocoder
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionInflater
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


class RestaurantDetailFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = RestaurantDetailFragment()
    }

    private lateinit var viewModel: RestaurantViewModel
    private lateinit var mapView: MapView
    private lateinit var infoLayout: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
                val detailView = RestaurantListFragment.newInstance()
                ft.replace(R.id.fragment_holder, detailView)
                ft.commit()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

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


        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        parentFragment?.startPostponedEnterTransition()
        val slideTransition = Slide()
        slideTransition.slideEdge = Gravity.START
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        viewModel.getSelectedRestaurant().observe(viewLifecycleOwner, { restaurant ->

            activity?.actionBar?.setTitle(restaurant.name)

            infoLayout.transitionName = restaurant.name
            infoLayout.findViewById<TextView>(R.id.address).setText(restaurant.location?.formatted_address)
            infoLayout.findViewById<TextView>(R.id.price_rating).setText(restaurant.price)
            infoLayout.findViewById<TextView>(R.id.phone_number).setText(restaurant.phone)

          //  val zoom = CameraUpdateFactory.zoomTo(15f)

            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(restaurant.location?.formatted_address, 1);

            if (addresses.size>0){
                val latitude = addresses.get(0).latitude
                val longitude = addresses.get(0).longitude

                val latLng = LatLng(latitude, longitude)
                val position = CameraPosition.Builder().target(latLng).zoom(15f).build()
                val center = CameraUpdateFactory.newLatLng(latLng)

                googleMap?.apply {
                    addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(restaurant.name)
                    )
                    moveCamera(CameraUpdateFactory.newCameraPosition(position))
                }
            }

            startPostponedEnterTransition()

        })
    }
}