package com.example.sureoutdoorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {
    private lateinit var map:GoogleMap
    private lateinit var btnCalculate:Button
    private var start: String = ""
    private var end: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var poly : Polyline? =null

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var currentLocation: Location? = null
    private val markerList = mutableListOf<Marker>()
    private var selectedLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)
        createFragment()
        btnCalculate = findViewById(R.id.btnCalculateRoute)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun createFragment() {
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.setOnMarkerClickListener(this)
        enableLocation()

        addCustomGymMarker(4.6308429975221745, -74.06460710544242, Color.BLUE, "Stark Smart Gym Calle 43")
        addCustomGymMarker(4.632483604225928, -74.06673794596544, Color.BLUE, "Palermmo 45")
        addCustomGymMarker(4.6335580575401165, -74.0636951693814, Color.BLUE, "Fight Club Bogota")

        btnCalculate.setOnClickListener{
            start = ""
            end = ""
            poly?.remove()
            poly = null

            getLastLocation()

            if(::map.isInitialized){

                if (start.isEmpty()) {
                    start = "${currentLocation?.longitude},${currentLocation?.latitude}"
                }

                if (end.isEmpty()) {
                    start = "${selectedLocation?.longitude}, ${selectedLocation?.latitude}"
                    createRoute()
                }
            }
        }
    }

    private fun addCustomGymMarker(lat: Double, lon: Double, color: Int, nombre: String) {
        val coordinates = LatLng(lat, lon)
        val markerOptions = MarkerOptions().position(coordinates).title(nombre)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(color))
        markerOptions.icon(bitmapDescriptor)
        val marker = map.addMarker(markerOptions)
        marker?.let{
            markerList.add(it)
        }
    }

    private fun getMarkerBitmap(color: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_gym)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable?.setTint(color)
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0,
            drawable?.intrinsicHeight ?: 0,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.draw(canvas)
        return bitmap
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if (!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }
        else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Se requieren los permisos!", Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }
            else{
                Toast.makeText(this, "No se acepto el permiso, para la localizacion en tiempo real debes dar el permiso desde ajustes", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "La localizacion fue desactivada, debes activarla desde los ajustes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Llevandote a tu localizacion actual", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Te encuentras en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerClick (marker:Marker): Boolean{
        if(markerList.contains(marker)){
            selectedLocation?.latitude = marker.position.latitude
            selectedLocation?.longitude = marker.position.longitude
        }
        return false
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(ApiService::class.java)
                .getRoute("5b3ce3597851110001cf6248f97cd04a1fc64c49bc37e29df74baa43",start,end)
            if (call.isSuccessful){
                drawRoute(call.body())
            }
            else {
                Log.i("Fallo", "La llamada al API no fue efectiva")
            }
        }
    }

    private fun drawRoute(routeResponse: RouteResponse?) {

        val polyLineOptions  = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polyLineOptions.add(LatLng(it[1], it[0]))
        }

        runOnUiThread {
            poly = map.addPolyline(polyLineOptions)
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {}
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                }
            }
    }
}