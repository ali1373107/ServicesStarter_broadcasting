package com.example.servicesmaps

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MainActivity : AppCompatActivity() {
    val vm: LocationViewModel by viewModels()
    var permissionsGranted = false
    var service:MyService? =null

    val serviceConn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?,binder: IBinder?) {
            service = (binder as MyService.MyServiceBinder).myService
        }
        override fun onServiceDisconnected(name:ComponentName?){

        }
    }
    // Add your service as an attribute of the main activity (nullable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Configuration.getInstance()
            .load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val map1 : MapView = findViewById(R.id.map1)
        map1.controller?.setZoom(14.0)
        map1.controller?.setCenter(GeoPoint(51.05, -0.72))
        requestPermissions()
        var btnStart = findViewById<Button>(R.id.btnStartGps)
        val receiver = object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.apply {
//inside the apply block "this" is refering to the intent
// //always check nullable of the intent with appy 0.0
                    when (this.action) {
                        "sendLocation" -> {
                         val lat1 = this.getDoubleExtra("lat", 0.0)
                            val lon1 = this.getDoubleExtra("lon",0.0)
                            vm.LatLonLiveData.postValue(LatLon(lat1,lon1))
                        }
                    }
                }
            }

        }
        val filter = IntentFilter().apply{
            addAction("sendLocation")
        }

        registerReceiver(receiver,filter)
        btnStart.setOnClickListener {
            val broadcastGps = Intent().apply{
                action = "sendGPS"
                putExtra("isGPSActive", true)
            }
            sendBroadcast(broadcastGps)

        }
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, LOCATION_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            permissionsGranted = true
            initService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = true
            initService() //why we call this function here// is it gonna bind the service or start the service
        } else {
            AlertDialog.Builder(this).setPositiveButton("OK", null).setMessage("GPS permission denied").show()
        }
    }

    fun initService() {

        val startIntent = Intent(this,MyService::class.java)
        startService(startIntent)

        val bindIntent = Intent(this,MyService::class.java);
        bindService(bindIntent,serviceConn, Context.BIND_AUTO_CREATE)
        // Start and bind the service here...
    }
}