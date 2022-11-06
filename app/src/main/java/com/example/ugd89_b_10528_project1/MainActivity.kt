package com.example.ugd89_b_10528_project1

import android.annotation.SuppressLint
import android.hardware.*
import android.hardware.Camera.CameraInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? =null
    private var mCameraView: CameraView?=null

    lateinit var sensorStatusTV : TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private var cameraId = Camera.CameraInfo.CAMERA_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorStatusTV= findViewById(R.id.sensorTV)

        try{
            mCamera= Camera.open()
        }catch (e:Exception){
            Log.d("Error", "Failed to get Camera" + e.message)
        }

        if(mCamera!=null){
            mCameraView= CameraView(this,mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)


            if (proximitySensor == null) {
                // on below line we are displaying a toast if no sensor is available
                Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // on below line we are registering
                // our sensor with sensor manager
                sensorManager.registerListener(
                    proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )

            }
        }

        @SuppressLint("MissingInflatedId", "LocalSuppress")
        val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{view: View?->System.exit(0)}

    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    // here we are setting our status to our textview..
                    // if sensor event return 0 then object is closed
                    // to sensor else object is away from sensor.

                    sensorStatusTV.text = "<<<<Near>>>>"

                    mCamera?.stopPreview()
                    mCamera?.release()

                    if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                        cameraId=Camera.CameraInfo.CAMERA_FACING_FRONT
                    }else{
                        cameraId=Camera.CameraInfo.CAMERA_FACING_BACK
                    }

                    
                    try{
                        mCamera= Camera.open(cameraId)
                    }catch (e:Exception){
                        Log.d("Error", "Failed to get Camera" + e.message)
                    }

                    mCamera?.startPreview()

                } else {
                    // on below line we are setting text for text view
                    // as object is away from sensor.
                    sensorStatusTV.text= "<<<<Away>>>>"
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        }
    }


}