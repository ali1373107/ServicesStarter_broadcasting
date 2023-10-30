package com.example.servicesmaps
import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.getSystemService
import org.osmdroid.util.GeoPoint

class MyService: Service(), LocationListener {

    val latLon = LatLon()
    inner class MyServiceBinder(val myService: MyService): android.os.Binder()
    override fun onStartCommand(intent:Intent?,flags:Int,startId: Int):Int{
        val filter = IntentFilter().apply{
            addAction("isGPSActive")
        }
        val gpsReceiver = object: BroadcastReceiver(){
            override fun onReceive(context:Context?,intent:Intent?){
                intent?.apply{
                    when (this.action){
                        "sendGPS"->{
                            val gps = this.getBooleanExtra("isGPSActive",false)
                            if(gps == true){
                                startGps()
                            }
                            else{
                                stopGps()
                            }
                        }
                    }
                }
            }
        }

        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return MyServiceBinder(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopGps()
    }

    @SuppressLint("MissingPermission")
     fun startGps() {
        val mgr =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0f,this)
    }

    fun stopGps() {
        val mgr =getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mgr.removeUpdates(this)
    }

    override fun onLocationChanged(loc: Location){
        latLon.lat = loc.latitude
        latLon.lon = loc.longitude
        val broadcast= Intent().apply{
            action = "sendLocation"
            putExtra("lat",latLon.lat)
            putExtra("lon",latLon.lon)
        }
        sendBroadcast(broadcast)

    }
    override fun onProviderEnabled(provider:String){

    }
    override fun onProviderDisabled(provider:String){

    }
    override fun onStatusChanged(provider:String?,status:Int,extras: Bundle?){

    }





}