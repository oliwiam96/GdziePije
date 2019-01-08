package com.oliwia.piwo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.location.Criteria
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oliwia.piwo.R.id.lookupTextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.*
import com.google.android.gms.maps.model.*
import com.oliwia.piwo.LocalisationService.FirebaseLocator

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.oliwia.piwo.DataStorage.BinaryStorageManager
import com.oliwia.piwo.LocalisationService.GPSManager
import com.oliwia.piwo.LocalisationService.Location
import com.oliwia.piwo.Permissions.PermissionsGuard
import com.oliwia.piwo.User.User


const val LOOKUP_TEXT = "LOOKUP_TEXT"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val permissionsGuard = PermissionsGuard(this, ::onPermissionGranted)
    val notificationManager = ToastNotificator(this)
    val mapsHandler = MapsHandler(::onMapReady)

    val zoomLevel = 10.0f
    val psychodelaX = 52.408210
    val psychodelaY = 16.935618

    val paulinaX = 52.408027
    val paulinaY = 16.9349535

    lateinit var psychodelaPolyline : Polyline
    var routeClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        gpsManager = GPSManager(::newUserPosition, permissionsGuard)
        // firebase
        signInSuccessCallback = { _ ->
            Log.i(TAG, "Sign in successfully")
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        addListenersToLookupTextView()
    }

    override fun onStart() {
        super.onStart()

        if(permissionsGuard.arePermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            Log.i(TAG, "Location permission granted")
            onPermissionGranted(PermissionsGuard.LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.i(TAG, "Location permission not granted")
            permissionsGuard.acquirePermissions()
        }
        // authentication
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    private fun newUserPosition(location: Location){
        Log.i(TAG, "Updating position to $location")
        mapsHandler.moveCameraOnPosition(location.toLatLng(), zoomLevel)
        currentUser.updateLocation(location, firebaseService) { user -> Log.i(TAG, "Updated position of ${user.username}")}
    }

    private fun onPermissionGranted(requestCode: Int)
    {
        Log.i(TAG, "Permission granted for $requestCode")
        when(requestCode)
        {
            PermissionsGuard.LOCATION_PERMISSION_REQUEST_CODE ->
            {
                // try to get get position from gps (inside this method we check whether we have permission)
                gpsManager.getPosition(this)

                val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(mapsHandler)
            }
        }
    }

    private fun addListenersToLookupTextView() {
        val lookupTextView = findViewById<EditText>(lookupTextView)
        lookupTextView.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    openLookupActivity(it)
                }
            }

        })
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight())
        val bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * 2, vectorDrawable.getIntrinsicHeight() * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    private fun onMapReady() {
        val paulina = LatLng(paulinaX, paulinaY)
        mapsHandler.addMarker(MarkerOptions()
                .position(paulina)
                .title("Paulina")
                .snippet("Piąteczek")
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_pin_circle_black_24dp)))


        val psychodela = LatLng(psychodelaX, psychodelaY)
        mapsHandler.addMarker(MarkerOptions().position(psychodela).title("Psychodela"))
        mapsHandler.moveCameraOnPosition(psychodela, zoomLevel)

        mapsHandler.setOnMarkerClickListener { marker ->

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
                val t = CalculationByDistance(currentUser.location.toLatLng(), LatLng(psychodelaX, psychodelaY))
                distanceText.text = String.format("%.2f", t) + "km"


                val infoButton = popupView.findViewById(R.id.info) as Button
                infoButton.setOnClickListener {
                    val i = Intent(this, DescriptionAcitivity::class.java)
                    // i.putExtra("URL",this.baseURL)
                    startActivityForResult(i, 10)
                }
                val routeButton = popupView.findViewById(R.id.routeButton) as Button
                routeButton.setOnClickListener {
                    val start = currentUser.location.toLatLng()
                    val end = LatLng(psychodelaX, psychodelaY)

                    if (!routeClicked) {
                        psychodelaPolyline = mapsHandler.addPolyline(PolylineOptions()
                                .add(start, end)
                                .width(5F)
                                .color(Color.RED))
                        routeClicked = true
                    }
                }


                    // dismiss the popup window when touched
                popupView.setOnTouchListener { v, event ->
                    if(routeClicked){
                    psychodelaPolyline.remove()
                    routeClicked = false}
                    popupWindow.dismiss()
                    true
                }
            } else {

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
                val t = CalculationByDistance(currentUser.location.toLatLng(), LatLng(paulinaX, paulinaY))
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
        if(routeClicked) {
            psychodelaPolyline.remove()
            routeClicked = false
        }
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
                mapsHandler.moveCameraOnPosition(LatLng(psychodelaX, psychodelaY), zoomLevel)
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_loc -> {
                if(currentUser != User.empty)
                    mapsHandler.moveCameraOnPosition(currentUser.location.toLatLng(), zoomLevel)
            }
            R.id.sign_in -> signIn()
            R.id.sign_out -> signOut()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        if(::psychodelaPolyline.isInitialized)
            psychodelaPolyline.remove()
        routeClicked = false
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
    // GOOGLE sign in
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                if(account == null || account.email == null)
                    java.lang.Exception("Empty account")

                val validatedEmail = FirebaseLocator.removeForbiddenCharactersFromEmail((account?.email)!!)
                setCurrentUser( User(validatedEmail) )
                Log.i(TAG, "Google sign in success, user ${currentUser.username}")
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)

                Toast.makeText(this.applicationContext, "Google sign in failed!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Use a different account with a valid e-mail.")
                currentUser = User.empty
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        Toast.makeText(this.applicationContext, "signed in!", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

                        Toast.makeText(this.applicationContext, "failed when signing in", Toast.LENGTH_SHORT).show()
                    }
                }
    }
    // [START signin]
    private fun signIn() {
        Log.i(TAG, "Application start, user $currentUser")
        val tempUser = userSerializer.load(userFilename) ?: User.empty

        if(currentUser == User.empty)
        {
            if(tempUser == User.empty) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            } else {
                setCurrentUser(tempUser)
            }
        }

    }

    private fun setCurrentUser(user: User){
        currentUser = user
        userSerializer.save(currentUser, userFilename)
        if(currentUser!= User.empty){
            // invoke callback
            if(::signInSuccessCallback.isInitialized)
                signInSuccessCallback.invoke(currentUser)
        }
    }
    // [END signin]

    private fun signOut() {
        if(currentUser == User.empty)
            return
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this.applicationContext, "signed out", Toast.LENGTH_SHORT).show()
            setCurrentUser(User.empty)
        }
    }

    private fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            Toast.makeText(this.applicationContext, "revoked access", Toast.LENGTH_SHORT).
                    show()
        }
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    // END Google sign in

    // firebase
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private val firebaseService = FirebaseLocator()

    private var currentUser = User.empty

    private val userFilename = "user_data.dat"
    private val userSerializer : BinaryStorageManager<User> by lazy{
        BinaryStorageManager<User>(applicationContext)
    }
    private lateinit var signInSuccessCallback : (User) -> Unit
    private lateinit var gpsManager: GPSManager

    companion object {
        const val TAG = "GdziePijeMainActivity"
        private const val RC_SIGN_IN = 9001
    }
}