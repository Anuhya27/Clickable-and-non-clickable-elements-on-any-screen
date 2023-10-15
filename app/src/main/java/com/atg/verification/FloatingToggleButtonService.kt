package com.atg.verification

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi

class FloatingToggleButtonService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var button: Button

    /**
     * Called when the service is created. Initializes the window manager, sets up the floating button,
     * and handles button click events.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        // Call the superclass onCreate method to ensure proper initialization of the service
        super.onCreate()

        // Initialize the WindowManager service to manage the UI elements
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Create a new Button widget and set its text to "Show View"
        button = Button(this)
        button.text = "Show View"

        // Set up the WindowManager parameters for the button
        setWindowManager(button)

        // Access the shared preferences and set the initial value of "isServiceEnabled" to false
        val editor = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).edit()
        editor.putBoolean("isServiceEnabled", false)
        editor.apply()

        // Log a message indicating that "isServiceEnabled" has been set to false
        Log.e("Float isServiceEnabled", "false")

        // Set a click listener for the button
        button.setOnClickListener {
            // Toggle the value of "isServiceEnabled"
            val isServiceEnabled = !button.isSelected

            // Update the shared preferences with the new value of "isServiceEnabled"
            val editor = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE).edit()
            editor.putBoolean("isServiceEnabled", isServiceEnabled)
            editor.apply()

            // Create an AlertDialog to display a message
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Title")
            builder.setMessage("Message")

            // Set a positive button click listener for the AlertDialog
            builder.setPositiveButton("OK") { dialog, which ->
                // Start the MyAccessibilityService when the user clicks "OK"
                val accessibilityServiceIntent = Intent(this, MyAccessibilityService::class.java)
                startService(accessibilityServiceIntent)
            }

            // Create and show the AlertDialog
            val dialog = builder.create()

            // Set the type of the window to TYPE_APPLICATION_OVERLAY to show it over other apps
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dialog.show()

            // Dismiss the AlertDialog after showing it
            dialog.dismiss()
        }
    }

    /**
     * Sets the window manager parameters for the given button.
     *
     * @param switchWidget The button for which window manager parameters are set.
     */
    private fun setWindowManager(switchWidget: Button) {
        // Create window manager parameters for the floating button view.
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, // Width of the view
                WindowManager.LayoutParams.WRAP_CONTENT, // Height of the view
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY // Use TYPE_APPLICATION_OVERLAY for Android Oreo and above
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE // Use TYPE_PHONE for versions below Android Oreo
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // The view should not receive focus
                PixelFormat.TRANSLUCENT // Use a translucent background for the view
        )

        // Set the gravity to position the view at the top left corner of the screen.
        params.gravity = Gravity.TOP or Gravity.START

        // Add the switchWidget (floating button) to the WindowManager using the specified parameters.
        windowManager.addView(switchWidget, params)

        // Log a message to indicate that the switchWidget has been added to the WindowManager.
        Log.d("Floating Button", "Switch Widget added to WindowManager")
    }

    /**
     * Returns null as there is no communication channel to this service.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return Null, as there is no communication channel to this service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Called when the service is destroyed. Stops the accessibility service and removes the floating button view.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (button.isSelected) {
            val accessibilityServiceIntent = Intent(this, MyAccessibilityService::class.java)
            stopService(accessibilityServiceIntent)
        }
        if (button.isAttachedToWindow) {
            windowManager.removeView(button)
        }
    }

}
