package com.example.servicesmaps


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// You'll find these imports useful for the network communication and JSON parsing

data class LatLon(var lat: Double=0.0, var lon: Double=0.0)

class LocationViewModel : ViewModel(){
    // Create a latLon property (of type LatLon) and corresponding LiveData, as last week

    val LatLonLiveData = MutableLiveData<LatLon>()

   var  latLon: LatLon = LatLon()
        set(value){
            field = value
            LatLonLiveData.value = value
        }


    // viewModel.latLon = LatLon(51, -1)

}