package com.livechat.view.maps

import android.os.Bundle
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
import com.livechat.databinding.ActivityViewLocationBinding
import com.livechat.extension.checkPermissions
import com.livechat.util.PermissionsUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-06-09
 * Time: 03:52 AM
 */
@AndroidEntryPoint
class ViewLocationActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityViewLocationBinding

    private lateinit var googleMap: GoogleMap

    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var selectedLatLng: LatLng? = null
    private var selectedName = ""
    private var selectedAddress = ""
    private var lastKnownLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = intent.getDoubleExtra(Constants.KEY_LAT, 0.0)
        val lng = intent.getDoubleExtra(Constants.KEY_LNG, 0.0)
        selectedLatLng = LatLng(lat, lng)
        selectedAddress = intent.getStringExtra(Constants.KEY_ADDRESS) ?: ""
        selectedName = intent.getStringExtra(Constants.KEY_NAME) ?: ""

        Places.initialize(this, Constants.MAPS_API_KEY)
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
    }

    override fun initView() {
        if (selectedName.isBlank()) {
            binding.tvTitle.setText(R.string.current_location)
        } else {
            binding.tvTitle.text = selectedName
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
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
                isZoomControlsEnabled = true
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

    private fun showAddress() {
        if (selectedLatLng == null) {
            return
        }

        val markerOptions = MarkerOptions().position(selectedLatLng!!).title(selectedAddress)

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                selectedLatLng!!,
                Constants.MAPS_DEFAULT_ZOOM
            )
        )
        lastKnownLocationMarker = googleMap.addMarker(markerOptions)
        lastKnownLocationMarker?.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLatLng!!))
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
        showAddress()
    }
}
