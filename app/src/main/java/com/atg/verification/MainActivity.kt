package com.atg.verification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is created. Initializes UI components and sets up button click events.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity layout from XML
        setContentView(R.layout.activity_main)

        // Find the button with ID "allowPermission" in the layout
        val allowPermission: Button = findViewById(R.id.allowPermission)

        // Log a message indicating that the button is clicked or the service is started
        Log.v("MyApp", "Button clicked or service started!")

        // Set a click listener on the button
        allowPermission.setOnClickListener(View.OnClickListener {
            // Get the shared preferences editor and set the "isServiceEnabled" flag to false
            val editor = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).edit()
            editor.putBoolean("isServiceEnabled", false)
            editor.apply()

            // Create an intent to open the Accessibility settings screen
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)

            // Start the Accessibility settings activity
            startActivity(intent)

            // Log a message indicating that the button is clicked and Accessibility settings are opened
            Log.v("MyApp", "Accessibility settings opened!")
        })
    }

    /**
     * Starts the floating toggle button service if permissions are granted.
     */
    private fun startService() {
        Log.v("MyApp", "Button clicked or service started!")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(Intent(this, FloatingToggleButtonService::class.java))
                } else {
                    startService(Intent(this, FloatingToggleButtonService::class.java))
                }
            }
        } else {
            startService(Intent(this, FloatingToggleButtonService::class.java))
        }
    }

    /**
     * Called when the activity is resumed. Checks for permissions again and starts the service.
     */
    override fun onResume() {
        super.onResume()
        startService()
    }

}

