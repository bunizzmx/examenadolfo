package com.example.android.examenadolfo.presentation.ui.map

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.examenadolfo.R
import com.example.android.examenadolfo.databinding.FragmentMapBinding

import com.example.android.examenadolfo.presentation.common.viewBinding
import com.example.android.examenadolfo.presentation.ui.tvs.UsersViewModel
import com.example.android.examenadolfo.utils.CONSTANTES.COLLECTION_GPS
import com.example.android.examenadolfo.utils.EventObserver
import com.example.android.examenadolfo.utils.treking.LocationsFirestore
import com.example.android.examenadolfo.utils.treking.TrackingService
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore


import java.util.*


class MapLocationsFragment  : Fragment() ,OnMapReadyCallback {

    private val authViewModel by activityViewModels<UsersViewModel>()
    private val binding by viewBinding(FragmentMapBinding::bind)
    var mMap: GoogleMap?=null
    var db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
        observeEvents()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db.collection(COLLECTION_GPS)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            if (mMap != null) {
                                val loc: LocationsFirestore = dc.document.toObject(LocationsFirestore::class.java)
                                val new_pin = LatLng(loc.lat.toDouble(), loc.lon.toDouble())
                            mMap?.addMarker(
                                MarkerOptions()
                                    .position(new_pin)
                                    .title("Fecha :" + loc.date)
                            )
                        }
                        }
                        DocumentChange.Type.MODIFIED -> Log.d("firestore", "Modified city: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d("firestore", "Removed city: ${dc.document.data}")
                    }
                }
            }



    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun applyBinding() {
        with(binding) {
            val fragment = childFragmentManager.findFragmentById(com.example.android.examenadolfo.R.id.mimap) as SupportMapFragment?
            fragment!!.getMapAsync(this@MapLocationsFragment)
        }
    }

    private fun observeEvents() {
        authViewModel.is_local.observe(viewLifecycleOwner, EventObserver {
            binding.numPoints.setText(getString(R.string.position_l) + " " + it)
        })
        authViewModel.mpins.observe(viewLifecycleOwner, EventObserver {
            var counter =0
            var total =0
            total = it.result.size()
            binding.numPoints.setText(getString(R.string.position_l) + " " + total)
            for (document in it.result) {
                if (mMap != null) {
                    val loc: LocationsFirestore = document.toObject(LocationsFirestore::class.java)
                    val new_pin = LatLng(loc.lat.toDouble(), loc.lon.toDouble())
                    counter++
                    if (counter >= total) {
                        mMap?.addMarker(
                            MarkerOptions()
                                .position(new_pin)
                                .title(getString(R.string.fecha) + " " + loc.date)
                                .snippet(getString(R.string.last_location) + " " + loc.lat + "/ " + loc.lon)
                                .icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_YELLOW
                                    )
                                )
                        )
                    } else {
                        mMap?.addMarker(
                            MarkerOptions()
                                .position(new_pin)
                                .snippet(getString(R.string.last_location) + " " + loc.lat + "/ " + loc.lon)
                                .title(getString(R.string.fecha) + " " + loc.date)
                        )
                    }

                    if (counter >= total)
                        mMap?.moveCamera(CameraUpdateFactory.newLatLng(new_pin))
                } else {
                    Log.d("df", "Error getting documents: ", it.exception)
                }
            }
        })
    }



    override fun onMapReady(googleMap: GoogleMap) {
        with(binding) {
            mMap = googleMap
            mMap!!.setInfoWindowAdapter(CustomInfoWindowAdapter(activity))
            mMap!!.setMinZoomPreference(16.0f)
            mMap!!.setMaxZoomPreference(20.0f)
            var current_location = TrackingService.getLastLocation()
            if(current_location!=null) {
                val sydney = LatLng(current_location.latitude, current_location.longitude)
                mMap?.addMarker(
                    MarkerOptions()
                        .position(sydney)
                        .title("Tu posicion")
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }
            authViewModel.getCurrent_pin()
        }

    }

   /* fun getCurrentPin(){
        db.collection(COLLECTION_GPS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var counter =0
                    var total =0
                    total = task.result.size()
                    with(binding) {
                        binding.numPoints.setText(getString(R.string.position_l) + " " + total)
                    }
                    for (document in task.result) {
                        if (mMap != null) {
                            val loc: LocationsFirestore = document.toObject(LocationsFirestore::class.java)
                            val new_pin = LatLng(loc.lat.toDouble(), loc.lon.toDouble())
                            counter++
                            if(counter >= total) {
                                mMap?.addMarker(
                                    MarkerOptions()
                                        .position(new_pin)
                                        .title(getString(R.string.fecha) + " " + loc.date)
                                        .snippet(getString(R.string.last_location) + " " + loc.lat + "/ " + loc.lon)
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_YELLOW))
                                )
                            }else{
                                mMap?.addMarker(
                                    MarkerOptions()
                                        .position(new_pin)
                                        .snippet(getString(R.string.last_location) + " " + loc.lat + "/ " + loc.lon)
                                        .title(getString(R.string.fecha) + " " + loc.date))
                            }

                            if(counter >= total)
                                mMap?.moveCamera(CameraUpdateFactory.newLatLng(new_pin))
                        }

                    }
                } else {
                    Log.d("df", "Error getting documents: ", task.exception)
                }
            }
    }
*/




}



