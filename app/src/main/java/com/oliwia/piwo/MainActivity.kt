package com.oliwia.piwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.DecimalFormat
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oliwia.piwo.R.id.lookupTextView
import com.oliwia.piwo.R.id.nav_view
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.view.View.OnTouchListener
import android.widget.*
import com.google.android.gms.maps.model.*
import kotlin.math.round


const val LOOKUP_TEXT = "LOOKUP_TEXT"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val zoomLevel = 16.0f
    var currentX = 0.0
    var currentY = 0.0

    val psychodelaX = 52.408210
    val psychodelaY = 16.935618

    val paulinaX = 52.408027
    val paulinaY = 16.9349535

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        addListenersToLookupTextView()
    }

    private fun addListenersToLookupTextView() {
        val lookupTextView = findViewById<EditText>(lookupTextView)
        lookupTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(enteredText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enteredText?.let {
                    openLookupActivity(it)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        val bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * 2, vectorDrawable.getIntrinsicHeight() * 2, Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Poznan and move the camera

        val paulina = LatLng(paulinaX, paulinaY)
        mMap.addMarker(MarkerOptions()
                .position(paulina)
                .title("Paulina")
                .snippet("Piąteczek")
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_pin_circle_black_24dp)))


        val psychodela = LatLng(psychodelaX, psychodelaY)
        mMap.addMarker(MarkerOptions().position(psychodela).title("Psychodela"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(psychodela, zoomLevel))
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);

        mMap.setMyLocationEnabled(true);

        if (mMap != null) {
            mMap.setOnMyLocationChangeListener { arg0 ->
                // TODO Auto-generated method stub
                currentX = arg0.latitude
                currentY = arg0.longitude

                // mMap.addMarker(MarkerOptions().position(LatLng(arg0.getLatitude(), arg0.getLongitude())).title("Ja"))
            }

        }
        val currentLocation = mMap.myLocation
        mMap.setOnMarkerClickListener { marker ->
            //  if (marker.title == "Psychodela"){
//                print("brawo")
//            } else{
//                print("slabo")
//            }
            // if marker source is clicked
            //   Toast.makeText(this@MainActivity, marker.title, Toast.LENGTH_SHORT).show()// display toast
            if (marker.title == "Psychodela") {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.pub_popup, null)

                // create the popup window
                val width = (LinearLayout.LayoutParams.WRAP_CONTENT).toInt()
                val height = (LinearLayout.LayoutParams.WRAP_CONTENT).toInt()
                val focusable = true // lets taps outside the popup also dismiss it
                val popupWindow = PopupWindow(popupView, width, height, focusable)

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(window.decorView.rootView, Gravity.BOTTOM, 0, 0)
                val distanceText = popupView.findViewById(R.id.distance) as TextView
                val t = CalculationByDistance(LatLng(currentX, currentY), LatLng(psychodelaX, psychodelaY))
                distanceText.text = String.format("%.2f", t) + "km"


                val infoButton = popupView.findViewById(R.id.info) as Button
                infoButton.setOnClickListener {
                    val i = Intent(this, DescriptionAcitivity::class.java)
                    // i.putExtra("URL",this.baseURL)
                    startActivityForResult(i, 10)
                }


                // dismiss the popup window when touched
                popupView.setOnTouchListener { v, event ->
                    popupWindow.dismiss()
                    true
                }
            } else{

                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.person_popup, null)

                // create the popup window
                val width = (LinearLayout.LayoutParams.WRAP_CONTENT).toInt()
                val height = (LinearLayout.LayoutParams.WRAP_CONTENT).toInt()
                val focusable = true // lets taps outside the popup also dismiss it
                val popupWindow = PopupWindow(popupView, width, height, focusable)

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(window.decorView.rootView, Gravity.BOTTOM, 0, 0)
                val distanceText = popupView.findViewById(R.id.distance_person) as TextView
                val t = CalculationByDistance(LatLng(currentX, currentY), LatLng(paulinaX, paulinaY))
                distanceText.text = String.format("%.2f", t) + "km"

                // dismiss the popup window when touched
                popupView.setOnTouchListener { v, event ->
                    popupWindow.dismiss()
                    true
                }

            }


            true
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_loc -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun openLookupActivity(enteredText: CharSequence) {
        val intent = Intent(this, com.oliwia.piwo.SearchView::class.java).apply {
            putExtra(LOOKUP_TEXT, enteredText)
        }
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec)

        return Radius * c
    }
}
