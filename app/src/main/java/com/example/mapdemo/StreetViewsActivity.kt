package com.example.mapdemo

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.LatLng

class StreetViewsActivity : AppCompatActivity() {

    private lateinit var streetViewPanoramaView: StreetViewPanoramaView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val position = intent.getIntExtra("position",0)
        val dataList = Constants.getPlaceList()
        val place = dataList[position]
        val latlng = LatLng(place.lat,place.lng)

        val options = StreetViewPanoramaOptions()
        savedInstanceState ?: options.position(latlng)
        streetViewPanoramaView = StreetViewPanoramaView(this, options)
        addContentView(
            streetViewPanoramaView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        //streetViewPanoramaView.onCreate(savedInstanceState?.getBundle(STREETVIEW_BUNDLE_KEY))

    }

    companion object {
        private const val STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey"
    }

//    public override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        var streetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY)
//        if (streetViewBundle == null) {
//            streetViewBundle = Bundle()
//            outState.putBundle(
//                STREETVIEW_BUNDLE_KEY,
//                streetViewBundle
//            )
//        }
//        streetViewPanoramaView.onSaveInstanceState(streetViewBundle)
//    }
}