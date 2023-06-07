package com.livechat.view.maps

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityMapsBinding
import com.livechat.extension.checkPermissions
import com.livechat.extension.getSimpleName
import com.livechat.extension.showSnackBar
import com.livechat.extension.visible
import com.livechat.util.PermissionsUtil
import com.livechat.view.maps.search_location.SearchLocationFragment
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

    private lateinit var binding: ActivityMapsBinding

    private var searchLocationFragment: SearchLocationFragment? = null

    private lateinit var googleMap: GoogleMap

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var selectedLatLng: LatLng? = null
    private var selectedAddress = ""
    private var lastKnownLocationMarker: Marker? = null

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
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgSearch.setOnClickListener {
            searchLocationFragment = SearchLocationFragment.newInstance()
            searchLocationFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentSearchLocation, it)
                    .addToBackStack(it.getSimpleName())
                    .commit()
            }
            binding.fragmentSearchLocation.visible()
        }

        binding.btnLocation.setOnClickListener {
            showSnackBar(binding.root, selectedAddress)
        }
    }

    override fun observeViewModel() {

    }

    private fun updateLocationUI() {
        try {
            googleMap.uiSettings.apply {
                isCompassEnabled = true
//                isIndoorLevelPickerEnabled = true
//                isMapToolbarEnabled = true
//                isZoomControlsEnabled = true
            }

            if (checkPermissions(PermissionsUtil.getLocationPermissions())) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

            } else {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                        task.result?.let {
                            updateLocation(LatLng(it.latitude, it.longitude))
                        }
                    } else {
                        Log.d("getDeviceLocation", "Current location is null. Using defaults.")
                        Log.e("getDeviceLocation", "Exception: %s", task.exception)
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLocation(latLng: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.MAPS_DEFAULT_ZOOM))
        showAddress(latLng)
    }

    private fun showAddress(latLng: LatLng) {
        getAddresses(latLng, onSuccess = { addresses ->
            if (addresses.isNullOrEmpty()) {
                return@getAddresses
            }

            try {
                val address = addresses[0]
                val markerOptions =
                    MarkerOptions().position(latLng).title(address.getAddressLine(0))

                selectedLatLng = latLng
                selectedAddress = address.getAddressLine(0)

                lastKnownLocationMarker?.remove()
                lastKnownLocationMarker = googleMap.addMarker(markerOptions)
                lastKnownLocationMarker?.showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun showAddress(latLng: LatLng, address: String) {
        selectedLatLng = latLng
        selectedAddress = address

        val markerOptions = MarkerOptions().position(latLng).title(address)

        lastKnownLocationMarker?.remove()
        lastKnownLocationMarker = googleMap.addMarker(markerOptions)
        lastKnownLocationMarker?.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun getAddresses(latLng: LatLng, onSuccess: (addresses: List<Address>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val geo = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1)
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
        updateLocationUI()
        getDeviceLocation()
    }

    fun pressBack() {
        onBackPressed()
    }
}
