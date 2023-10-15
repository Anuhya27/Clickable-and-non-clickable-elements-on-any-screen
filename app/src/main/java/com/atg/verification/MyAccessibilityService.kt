package com.atg.verification

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ProgressBar
import androidx.annotation.RequiresApi

class MyAccessibilityService : AccessibilityService() {

    // Lazy initialization of WindowManager
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }

    // Data structures to hold accessibility nodes and drawn rectangles
    private val nodesToDraw: HashSet<AccessibilityNodeInfo> = HashSet()
    private val newRectangleViews: MutableList<View> = mutableListOf()
    private val rectangleViews: MutableList<View> = mutableListOf()

    // Processing indicator
    private var processingIndicator: ProgressBar? = null

    // Handler for delayed tasks
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * Handles the accessibility events received by the service.
     * It processes the events, extracts necessary information, and initiates the drawing of rectangles.
     * @param event The accessibility event triggered in the system.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        // Access SharedPreferences to check if the service is enabled
        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val isServiceEnabled = sharedPreferences.getBoolean("isServiceEnabled", false)

//        Log.e("Acessi isServiceEnabled", isServiceEnabled.toString())

        // Check if the service is enabled and the event type is TYPE_WINDOW_STATE_CHANGED
        if (isServiceEnabled && event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // Show the processing indicator on the screen
            showProcessingIndicator()

            // Post a delayed task to handle the accessibility tree processing
            handler.postDelayed({
                val rootNode = rootInActiveWindow
                rootNode?.let {

                    // Log node information for debugging purposes
                    Log.d("Node Info: ", it.toString())

                    // Clear previously drawn rectangles
                    clearPreviousRectangles()

                    // Traverse the accessibility tree to find nodes to draw rectangles around
                    traverseAccessibilityTree(it, 0)

                    // Add new rectangles to the screen
                    addNewRectangles()

                    // Draw the rectangles on the screen
                    drawRectangles()

                    // Hide the processing indicator after processing is complete
                    hideProcessingIndicator()
                }
            }, 500) // 500 milliseconds delay
        }
    }

    /**
     * Handles interruptions of the accessibility service.
     * This function is called when the service is interrupted.
     */
    override fun onInterrupt() {
        Log.e("Accessibility Service", "Service Interrupted")
    }

    /**
     * Called when the accessibility service is successfully connected.
     * It configures the service based on the shared preferences of the application.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        // Call the parent class's onServiceConnected method
        super.onServiceConnected()

        // Get the SharedPreferences object for the application preferences
        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

        // Retrieve the 'isServiceEnabled' flag from the shared preferences
        val isServiceEnabled = sharedPreferences.getBoolean("isServiceEnabled", false)

        // Check if the service is enabled in the shared preferences
        if (isServiceEnabled) {
            // If the service is enabled, configure the accessibility service
            // Create a new AccessibilityServiceInfo object
            val info = AccessibilityServiceInfo()

            // Set the event types the service will listen for (window state changes)
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED

            // Set the feedback type for the service (spoken feedback)
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN

            // Apply the configured AccessibilityServiceInfo to the service
            serviceInfo = info

            // Log that the service has been configured
            Log.d("MyAccessibilityService", "Accessibility service configured with event type: TYPE_WINDOW_STATE_CHANGED")
        } else {
            // If the service is not enabled, log that the service is not configured
            Log.d("MyAccessibilityService", "Accessibility service not configured because it's not enabled in shared preferences")
        }
    }

    /**
     * Recursively traverses the accessibility tree to find nodes to draw rectangles around.
     * @param nodeInfo The current node being processed in the accessibility tree.
     * @param depth The depth of the current node in the tree.
     */
    private fun traverseAccessibilityTree(nodeInfo: AccessibilityNodeInfo, depth: Int) {
        // Check if the depth exceeds the maximum allowed depth
        if (depth >= MAX_DEPTH) {
            // Log a message indicating the node is skipped due to maximum depth reached
            Log.d("AccessibilityService", "Node skipped: Maximum depth reached.")
            return
        }

        // Add the current node to the nodesToDraw set
        nodesToDraw.add(nodeInfo)

        // Loop through the child nodes and recursively traverse the tree
        for (i in 0 until nodeInfo.childCount) {
            val childNode = nodeInfo.getChild(i)
            childNode?.let {
                // Recursive call to traverse the child node with increased depth
                traverseAccessibilityTree(it, depth + 1)
            }
        }
    }

    /**
     * Adds new rectangles to the screen based on the nodes extracted from the accessibility tree.
     */
    private fun addNewRectangles() {

        // Draw rectangles around nodes in nodesToDraw list with green color, then clear the list.
        for (node in nodesToDraw) {
            // Draw rectangles into the newRectangleViews list
            drawRectangle(node, Color.GREEN, newRectangleViews)
        }

        // Clear the nodesToDraw list after drawing rectangles.
        nodesToDraw.clear()
//        Log.e("done drawing", "rectangles") // Log a message indicating that rectangles have been drawn.

        // Delay the execution of the following code block by 3000 milliseconds (3 seconds).
        handler.postDelayed({
            // Clear previously drawn rectangles from the screen.
            clearPreviousRectangles()

            // Retrieve shared preferences and disable the service by setting "isServiceEnabled" to false.
            val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isServiceEnabled", false)
            editor.apply() // Apply the changes to shared preferences.

            Log.e("changing back", "isServiceEnabled") // Log a message indicating that service is disabled.

        }, 3000) // 3000 milliseconds = 3 seconds delay before executing this code block.

    }

    private fun drawRectangles() {
        rectangleViews.addAll(newRectangleViews) // Add new rectangles to the existing list
        newRectangleViews.clear() // Clear the new rectangles list
    }

    /**
     * Draws rectangles on the screen based on the given AccessibilityNodeInfo.
     * @param nodeInfo The node around which the rectangle is drawn.
     * @param color The color of the rectangle.
     * @param targetList The list where the newly drawn rectangle is added.
     */
    private fun drawRectangle(nodeInfo: AccessibilityNodeInfo, color: Int, targetList: MutableList<View>) {
        // Retrieve the bounds of the accessibility node in screen coordinates
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)

        // Get the root node of the active window; return if null
        val rootNode = rootInActiveWindow ?: run {
            Log.e("Accessibility", "Root node is null.")
            return
        }

        // Calculate the offset based on the root node's position
        val offsetX = Rect().apply { rootNode.getBoundsInScreen(this) }.left
        val offsetY = Rect().apply { rootNode.getBoundsInScreen(this) }.top

        // Adjust the rectangle's position considering the offset and a vertical shift
        rect.offset(-offsetX, -offsetY - 80)

        // Create a new view for drawing the rectangle
        val rectangleView = View(this)

        // Set the background drawable based on node clickability
        if (nodeInfo.isClickable) {
            rectangleView.setBackgroundResource(R.drawable.bordered_rectangle) // Use the custom border drawable for clickable nodes
        } else {
            rectangleView.setBackgroundResource(R.drawable.bordered_rectangle_red) // Use the custom border drawable for non-clickable nodes
        }

        // Set layout parameters for the rectangle view
        val params = WindowManager.LayoutParams(
                rect.width(),
                rect.height() - 25,
                rect.left,
                rect.top - 25,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        // Set gravity for the rectangle view
        params.gravity = Gravity.START or Gravity.TOP

        // Add the rectangle view to the window manager and target list
        windowManager.addView(rectangleView, params)
        targetList.add(rectangleView)

        Log.i("Accessibility", "Rectangle drawn at (${rect.left}, ${rect.top}) with size ${rect.width()} x ${rect.height()}")
    }

    /**
     * Clears previously drawn rectangles from the screen.
     */
    private fun clearPreviousRectangles() {
        // Remove all rectangles from the screen and clear the list of rectangle views.
        for (view in rectangleViews) {
            windowManager.removeView(view) // Remove each rectangle view from the window manager.
        }
        rectangleViews.clear() // Clear the list of rectangle views.

        // Access the shared preferences to update the service status.
        val sharedPreferences = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

        // Modify the shared preferences to set the service status to disabled.
        val editor = sharedPreferences.edit()
        editor.putBoolean("isServiceEnabled", false) // Set the 'isServiceEnabled' flag to false.
        editor.apply() // Apply the changes to the shared preferences.

        // Log a message indicating that rectangles are cleared and service status is disabled.
        Log.d("MyAccessibilityService", "Rectangles cleared and service is disabled.")
    }

    /**
     * Displays a processing indicator on the screen while processing the accessibility events.
     */
    private fun showProcessingIndicator() {
        // Check if processingIndicator is null
        if (processingIndicator == null) {
            // Create a new ProgressBar instance with the context of this service
            processingIndicator = ProgressBar(this)

            // Set the layout parameters for the ProgressBar
            val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            )

            // Set gravity to center the ProgressBar on the screen
            params.gravity = Gravity.CENTER

            // Add the processingIndicator to the window manager with the specified parameters
            windowManager.addView(processingIndicator, params)
        }
    }

    /**
     * Hides the processing indicator from the screen after processing is complete.
     */
    private fun hideProcessingIndicator() {
        // Check if processing indicator is not null
        processingIndicator?.let {

            // Remove the processing indicator view from the WindowManager
            windowManager.removeView(it)

            // Set processingIndicator to null after removal
            processingIndicator = null
        }
    }

    /**
     * Handles the setup and configuration of the accessibility service.
     * It checks the shared preferences and configures the service accordingly.
     */
    companion object {
        private const val MAX_DEPTH = 15
    }
}
