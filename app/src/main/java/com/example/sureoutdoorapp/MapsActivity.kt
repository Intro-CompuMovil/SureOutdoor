package com.example.sureoutdoorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationRequest
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.sureoutdoorapp.ApiService
import com.example.sureoutdoorapp.R
import com.example.sureoutdoorapp.RouteResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener, OnMyLocationClickListener, GoogleMap.OnMarkerClickListener{

    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mCurrentLocation: Location? = null

    private lateinit var btnCalculate:Button
    private var start: String = ""
    private var end: String = ""

    var poly : Polyline? =null

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.setOnMarkerClickListener(this)

        enableLocation()

        addCustomGymMarker(4.6308429975221745, -74.06460710544242, "Stark Smart Gym Calle 43")
        addCustomGymMarker(4.632483604225928, -74.06673794596544, "Palermmo 45")
        addCustomGymMarker(4.6335580575401165, -74.0636951693814, "Fight Club Bogota")
        addCustomGymMarker(4.633766349340839, -74.06471238506033, "Personal Fitness Club")
        addCustomGymMarker(4.635911537626556, -74.06357185226817, "DukBox")
        addCustomGymMarker(4.627189694746341, -74.0624767852642, "Centro Javeriano De Formacion Deportiva")
        addCustomGymMarker(4.621157532643943, -74.06745823746255, "Anytime Fitness 24/7")
        addCustomGymMarker(4.618763834262762, -74.0678595412154, "Smart Fit San Martin")
        addCustomGymMarker(4.644815359064711, -74.06424122384759, "Healthy Gym")

        addCustomParkMarker(4.61716652886119, -74.06483083418829, "Gimnasio Al Aire Libre")
        addCustomParkMarker(4.623772523018284, -74.07169681719019, "Parque Teusaquillo")
        addCustomParkMarker(4.630577217809953, -74.06661924272204, "Parque Publico Carlos E. Restrepo")
        addCustomParkMarker(4.638967007047057, -74.07280716782932, "Parque Distrital Alfonso Lopez")
        addCustomParkMarker(4.645424684913661, -74.06186091899018, "Parque De Los Hippies")
        addCustomParkMarker(4.610714925431484, -74.08421614034967, "Parque Publico Vecinal Urbanizacion El Liston")

        btnCalculate.setOnClickListener{
            start = ""
            end = ""
            poly?.remove()
            poly = null

            if(::map.isInitialized){

                if (start.isEmpty()) {
                    if(isLocationPermissionGranted()){
                        fusedLocationClient.lastLocation.addOnSuccessListener{ location ->
                            start = "${location?.longitude},${location?.latitude}"
                            Log.i ("Loc LAT", "${location.latitude}")
                            Log.i ("Loc LON", "${location.longitude}")
                        }
                    }
                }
                if (end.isEmpty()) {
                    end = "${selectedLocation?.longitude},${selectedLocation?.latitude}"
                }
                createRoute()

            }
        }
    }

    private fun addCustomGymMarker(lat: Double, lon: Double, nombre: String) {
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.mipmap.ic_gym_map)
        val bitmap: Bitmap? = drawable?.toBitmap()
        val icon: BitmapDescriptor? = bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
        val coordinates = LatLng(lat, lon)
        val markerOptions = MarkerOptions().position(coordinates).title(nombre)
        markerOptions.icon(icon)
        val marker = map.addMarker(markerOptions)
        marker?.let{
            markerList.add(it)
        }
    }

    private fun addCustomParkMarker(lat: Double, lon: Double, nombre: String) {
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.mipmap.ic_park_m)
        val bitmap: Bitmap? = drawable?.toBitmap()
        val icon: BitmapDescriptor? = bitmap?.let { BitmapDescriptorFactory.fromBitmap(it) }
        val coordinates = LatLng(lat, lon)
        val markerOptions = MarkerOptions().position(coordinates).title(nombre)
        markerOptions.icon(icon)
        val marker = map.addMarker(markerOptions)
        marker?.let{
            markerList.add(it)
        }
    }

    private fun Drawable.toBitmap(): Bitmap? {
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return null
        }
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth,
            intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
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
            val location = Location("")
            location.latitude = marker.position.latitude
            location.longitude = marker.position.longitude

            selectedLocation = location
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
            Log.i("Start", start)
            Log.i("End", end)
            if (call.isSuccessful){
                drawRoute(call.body())
            }
            else {
                /*Log.i("Start", start)
                Log.i("End", end)*/
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
}