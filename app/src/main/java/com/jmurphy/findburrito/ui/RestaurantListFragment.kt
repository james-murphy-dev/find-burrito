package com.jmurphy.findburrito.ui

import GetRestaurantsQuery
import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jmurphy.findburrito.R
import com.jmurphy.findburrito.RestaurantViewModel


/**
 * A fragment representing a list of restaurants.
 */
class RestaurantListFragment : Fragment(), OnItemClickListener{

    val LOCATION_REQ = 100
    private lateinit var viewModel: RestaurantViewModel
    private var adapter = RestaurantListAdapter(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var permissionBtn: MaterialButton
    private lateinit var permissionDeniedMessage: RelativeLayout

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // load nearby restaurants
            loadRestaurants()
        } else {
            // show permission denied message
            progressBar.visibility = GONE
            permissionDeniedMessage.visibility = VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(RestaurantViewModel::class.java)

        val view = inflater.inflate(R.layout.item_list, container, false)

        permissionDeniedMessage = view.findViewById(R.id.permission_denied_message)
        permissionBtn = view.findViewById(R.id.permission_btn)
        progressBar = view.findViewById(R.id.progress)
        recyclerView = view.findViewById(R.id.list)
        recyclerView.adapter = adapter

        permissionBtn.setOnClickListener {

            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context?.getPackageName(), null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        // prompt user for location permission
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                loadRestaurants()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
                val alert = MaterialAlertDialogBuilder(requireContext())
                alert.setTitle("Location permission needed")
                    .setMessage("We need your current location to display the nearby restaurants")
                    .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->

                    }.show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        return view
    }

    override fun onItemClicked(view: View, restaurant: GetRestaurantsQuery.Business) {

        viewModel.setSelectedRestaurant(restaurant)

        val detailView = RestaurantDetailFragment.newInstance()

        val slideTransition = Slide()
        slideTransition.slideEdge = Gravity.END
        detailView.enterTransition = slideTransition
        detailView.sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(
            android.R.transition.move
        )

        parentFragmentManager.beginTransaction()
            .addSharedElement(view, view.transitionName)
            .replace(R.id.fragment_holder, detailView)
            .addToBackStack(null)
            .commit()
    }

    fun loadRestaurants(){
        viewModel.getNearbyRestaurants().observe(viewLifecycleOwner, { list ->
            progressBar.visibility = GONE
            recyclerView.visibility = VISIBLE
            adapter.setData(list)
        })
    }

    companion object {
        fun newInstance() = RestaurantListFragment()
    }
}