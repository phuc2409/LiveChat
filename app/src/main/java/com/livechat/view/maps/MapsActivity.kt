package com.livechat.view.maps

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityMapsBinding
import com.livechat.extension.checkPermissions
import com.livechat.util.PermissionsUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 02:37 AM
 */
@AndroidEntryPoint
class MapsActivity : BaseActivity(), OnMapReadyCallback {

    companion object {
        private val DEFAULT_ZOOM = 15f
    }

    private lateinit var binding: ActivityMapsBinding

    private lateinit var googleMap: GoogleMap

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Places.initialize(this, Constants.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
    }

    override fun initView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }

    private fun updateLocationUI() {
        try {
            googleMap.uiSettings.apply {
                isCompassEnabled = true
                isIndoorLevelPickerEnabled = true
                isMapToolbarEnabled = true
                isZoomControlsEnabled = true
            }

            if (checkPermissions(PermissionsUtil.getLocationPermissions())) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

            } else {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var lastKnownLocation: Location? = null

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (checkPermissions(PermissionsUtil.getLocationPermissions())) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ),
                                    DEFAULT_ZOOM
                                )
                            )
                            showAddress(lastKnownLocation!!)
                        }
                    } else {
                        Log.d("getDeviceLocation", "Current location is null. Using defaults.")
                        Log.e("getDeviceLocation", "Exception: %s", task.exception)

                        val sydney = LatLng(-34.0, 151.0)
                        googleMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(
                                    sydney,
                                    DEFAULT_ZOOM
                                )
                        )
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun showAddress(location: Location) {
        getAddresses(location, onSuccess = { addresses ->
            if (addresses.isNullOrEmpty()) {
                return@getAddresses
            }

            try {
                val address = addresses[0]
                binding.tvCurrentLocation.text = address.getAddressLine(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun getAddresses(location: Location, onSuccess: (addresses: List<Address>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val geo = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses = geo.getFromLocation(location.latitude, location.longitude, 1)
            withContext(Dispatchers.Main) {
                onSuccess(addresses)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        this.googleMap.addMarker(
//            MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//        )
//        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        updateLocationUI()
        getDeviceLocation()
    }
}
