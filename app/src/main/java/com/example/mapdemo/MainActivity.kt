package com.example.mapdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable.ArrowDirection
import androidx.core.content.ContextCompat
import com.example.mapdemo.databinding.ActivityMainBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var binding:ActivityMainBinding? = null
    private var mGoogleMap:GoogleMap? = null
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private var mLat = -33.87365
    private var mLng = 151.20689

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Places.initialize(applicationContext, getString(R.string.map_api_key))
        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.ADDRESS,Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLong = place.latLng!!
                mLat = latLong.latitude
                mLng = latLong.longitude
                zoomOnMap(latLong)
            }

            override fun onError(status: Status) {
                Toast.makeText(this@MainActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                Log.i("PlaceError", "An error occurred: $status")
            }
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapOptionButton:ImageButton = findViewById(R.id.mapOptionsMenu)
        val popupMenu = PopupMenu(this,mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_options,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }

        binding?.fabStarMark?.setOnClickListener {
            markStarPlace()
        }

        binding?.fabStreetView?.setOnClickListener {
            val intent = Intent(this, FamousPlaceActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        addMarker(LatLng(12.345,23.232))
        addCustomMarker(R.drawable.location_pin_24dp, LatLng(12.543,23.432))
        addDraggableMarker(LatLng(12.987,23.789))

        mGoogleMap?.setOnMapClickListener {
           addCircle(it)
        }

        mGoogleMap?.setOnMapLongClickListener {
            addCustomMarker(R.drawable.location_pin_24dp,it)
        }
        mGoogleMap?.setOnMarkerClickListener {
            it.remove()
            false
        }

        drawLines()
        zoomOnMap(LatLng(70.0, 75.903))
        addMarker(LatLng(-23.684, 133.903))
        mGoogleMap?.setOnPolylineClickListener {
            it.color = -0x00bb00
        }

        drawPolygon()
        mGoogleMap?.setOnPolygonClickListener {
            it.strokeColor = -0x00ff00
        }


        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.boy_trash_throw))
            .position(LatLng(4.566,5.345),100f)

        mGoogleMap?.addGroundOverlay(androidOverlay)

        addHeatMap()
    }

    private fun changeMap(id:Int)
    {
        when(id)
        {
            R.id.normal_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    private fun zoomOnMap(mLatLng: LatLng)
    {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(mLatLng,6f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun addMarker(position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions().position(position)
            .title("Marker")
            .snippet("Snippet")
        )
    }

    private fun addCustomMarker(iconImage: Int, position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions().position(position)
            .draggable(true)
            .title("Custom Marker")
            .snippet("Snippet")
            .icon(BitmapDescriptorFactory.fromResource(iconImage))
        )
    }
    private fun addDraggableMarker(position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions().position(position)
            .draggable(true)
            .title("Draggable Marker")
            .snippet("Snippet")
        )
    }

    private var circle:Circle? = null
    private fun addCircle(centre:LatLng)
    {
        circle?.remove()
        circle = mGoogleMap?.addCircle(CircleOptions()
            .center(centre)
            .radius(1000.0)
            .strokeWidth(8f)
            .strokeColor(Color.parseColor("#FF0000"))
            .fillColor(ContextCompat.getColor(this,R.color.blue))
        )
    }

    private var isStarMarked = false
    private var starMark:Marker? = null
    private fun markStarPlace()
    {
        val starPlace = LatLng(2.345,3.432)
        if (!isStarMarked)
        {
            isStarMarked = true
            starMark = mGoogleMap?.addMarker(MarkerOptions()
                .position(starPlace))
        }
        else
        {
            starMark?.remove()
            isStarMarked = false
        }
    }

    private fun drawLines()
    {
        val DOT: PatternItem = Dot()
        val GAP: PatternItem = Gap(20f)
        // val DASH

        val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)

        val polyline = mGoogleMap?.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(Constants.getAstLatLong())
                .endCap(ButtCap())
                .startCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.right_arrow)))
                .color(ContextCompat.getColor(this,R.color.blue))
                .jointType(JointType.BEVEL)
                .width(12f)
                .pattern(PATTERN_POLYLINE_DOTTED)
        )
    }

    private fun drawPolygon()
    {
        val DOT: PatternItem = Dot()
        val GAP: PatternItem = Gap(20f)
        val DASH : PatternItem = Dash(20f)
        val PATTERN_POLYGON_DOT_DASH = listOf(GAP, DOT, DASH,DOT)
        val polygon = mGoogleMap?.addPolygon(
            PolygonOptions()
            .clickable(true)
            .addAll(Constants.getStarCord())
                .fillColor(-0xff00ff)
                .strokeColor(-0xffaabb)
                .strokeWidth(20f)
                .strokePattern(PATTERN_POLYGON_DOT_DASH)
        )
    }

    private fun addHeatMap()
    {
        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(Constants.getHeatmapWeightedData())
            .radius(20)
            .maxIntensity(1000.0)
            .build()
        mGoogleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
    }
}